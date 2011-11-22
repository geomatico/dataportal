/**
 * 
 */
package test.dataportal.csw;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.dataportal.csw.CSWNamespaceContext;
import org.dataportal.csw.Filter;
import org.dataportal.csw.GetRecords;
import org.dataportal.csw.Operator;
import org.dataportal.csw.Property;
import org.dataportal.csw.SortBy;

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
		getrecords.setTypeNames("gmd:MD_Metadata");
		getrecords.setElementSetName("full");
		
		Filter filtro = new Filter();
		
		// creating rules
		Property escomo = new Property("PropertyIsLike");	
		Property menorque = new Property("PropertyMinorThan");
		
		try {

			ArrayList<String> rules = new ArrayList<String>();

			escomo.setLiteral("un valor");
			escomo.setPropertyName("una propiedad");	
			rules.add(escomo.getExpresion());
			menorque.setLiteral("valor menor");
			menorque.setPropertyName("propiedad menor");
			rules.add(menorque.getExpresion());
			Operator and = new Operator("And");
			and.setRules(rules);
			
			Operator or = new Operator("Or");
			ArrayList<String> orRules = new ArrayList<String>();
			Property mayorque = new Property("PropertyGreatherThan");
			mayorque.setPropertyName("una propiedad mayor");
			mayorque.setLiteral("un literal mayor");
			orRules.add(and.getExpresion());
			orRules.add(mayorque.getExpresion());
			or.setRules(orRules);
			
			ArrayList<String> filterRules = new ArrayList<String>();
			filterRules.add(or.getExpresion());
			
			filtro.setRules(filterRules);

			getrecords.setFilter(filtro);
			
			SortBy sortby = new SortBy();
			sortby.setPropertyName("title");
			sortby.setOrder(SortBy.ASC);
			
			getrecords.setSortby(sortby);
			
			getrecords.getExpresion();
			
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		//fail("Not yet implemented");
	}

}
