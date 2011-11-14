/**
 * 
 */
package test.dataportal.csw;

import javax.xml.stream.XMLStreamException;

import org.dataportal.csw.CSWNamespaceContext;
import org.dataportal.csw.Filter;
import org.dataportal.csw.Property;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 *
 */
public class FilterTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * Test method for {@link org.dataportal.csw.Filter#getExpresion()}.
	 */
	public void testGetExpresion() {
		
		// TODO funcionality only launch the Filter
		
		Filter filtro = new Filter();
		CSWNamespaceContext namespacecontext = new CSWNamespaceContext();
		filtro.setNamespacecontext(namespacecontext);
		
		// creating rules
		Property mayorque = new Property("PropertyIsLike");	
		
		try {

			mayorque.setLiteral("un valor");
			mayorque.setPropertyName("una propiedad");			
			filtro.setRules(mayorque.getExpresion());
			filtro.getExpresion();
			
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		//fail("Not yet implemented");
	}

}
