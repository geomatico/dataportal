package cmima.icos;

import cmima.icos.utils.RangeDate;
import junit.framework.TestCase;

public class RangeDateTest extends TestCase {

	private RangeDate rangedate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		rangedate = new RangeDate("2011-07-10T12:00:00Z",
				"2011-07-15T12:00:00Z");
	}

	public void testGetStart_date() {
		assertEquals(rangedate.getStart_date(), "2011-07-10T12:00:00Z");
	}

	public void testGetEnd_date() {
		assertEquals(rangedate.getEnd_date(), "2011-07-15T12:00:00Z");
	}

	public void testToOGCTemporalExtent() {
		String rangeTest = "<ogc:And>\n<ogc:PropertyIsGreaterThanOrEqualTo>\n<ogc:PropertyName>TempExtent_begin</ogc:PropertyName>\n"
				+ "<ogc:Literal>2011-07-10T12:00:00Z</ogc:Literal>\n</ogc:PropertyIsGreaterThanOrEqualTo>\n"
				+ "<ogc:PropertyIsLessThanOrEqualTo>\n<ogc:PropertyName>TempExtent_end</ogc:PropertyName>\n"
				+ "<ogc:Literal>2011-07-15T12:00:00Z</ogc:Literal>\n"
				+ "</ogc:PropertyIsLessThanOrEqualTo>\n</ogc:And>\n";
		assertEquals(rangeTest, rangedate.toOGCTemporalExtent());
	}

}
