/**
 * 
 */
package test.dataportal.utils;

import java.util.ArrayList;

import org.dataportal.utils.BBox;
import org.dataportal.utils.Utils;


import junit.framework.TestCase;

/**
 * @author Micho Garcia
 *
 */
public class UtilsTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link org.dataportal.utils.Utils#extractToBBoxes(java.lang.String)}.
	 */
	public void testExtractToBBoxes() {
		String stringBBoxes = "[[10,20,10,40]]";
		ArrayList<BBox> testBBoxes = Utils.extractToBBoxes(stringBBoxes);
		assertEquals("10", testBBoxes.get(0).getXmin());
		assertEquals("20", testBBoxes.get(0).getYmin());
		assertEquals("10", testBBoxes.get(0).getXmax());
		assertEquals("40", testBBoxes.get(0).getYmax());
	}

}
