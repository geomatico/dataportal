package test.dataportal;

import org.dataportal.DataPortalError;

import junit.framework.TestCase;

public class DataPortalErrorTest extends TestCase {

	DataPortalError error = new DataPortalError();
	
	protected void setUp() throws Exception {		
		
		error.setMessage("test.message");
		error.setCode("test.code");
		super.setUp();
	}

	public void testGetMessage() {
		assertEquals("test.message", error.getMessage());
	}

	public void testGetCode() {
		assertEquals("test.code", error.getCode());
	}
}
