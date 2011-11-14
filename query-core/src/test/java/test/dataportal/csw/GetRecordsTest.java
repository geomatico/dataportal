/**
 * 
 */
package test.dataportal.csw;

import javax.xml.stream.XMLStreamException;

import org.dataportal.csw.CSWNamespaceContext;
import org.dataportal.csw.GetRecords;

import junit.framework.TestCase;

/**
 * @author michogar
 * 
 */
public class GetRecordsTest extends TestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link org.dataportal.csw.GetRecords#getExpresion()}.
	 */
	public void testGetExpresion() {
		
		// TODO funcionality only launch GetRecords
		
		
		GetRecords getrecords = new GetRecords();
		CSWNamespaceContext namespacecontext = new CSWNamespaceContext();
		getrecords.setNamespacecontext(namespacecontext);
		getrecords.setTypeNames("gmd:MD_Metadata");
		getrecords.setElementSetName("full");
		try {
			getrecords.getExpresion();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

}
