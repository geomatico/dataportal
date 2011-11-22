package test.dataportal.csw;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.dataportal.csw.GetRecordById;

import junit.framework.TestCase;

public class GetRecordByIdTest extends TestCase {
	
	private GetRecordById getRecordById = null;

	protected void setUp() throws Exception {
		
		getRecordById = new GetRecordById("brief");
		super.setUp();
	}

	public void testCreateQuery() {
		ArrayList<String> ides = new ArrayList<String>();
		ides.add("icos-sample-1");
		ides.add("icos-sample-2");
		try {
			System.out.print(getRecordById.createQuery(ides));
		}catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

}
