/**
 * 
 */
package test.dataportal;

import java.io.InputStream;

import org.dataportal.DownloadController;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class DownloadControllerTest extends TestCase {

	private DownloadController controladorDescarga = new DownloadController();

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for
	 * {@link org.dataportal.DownloadController#askgn2Download(java.util.InputStream)}
	 * .
	 */
	public void testAskgn2download() {		
		
		InputStream isRequestXML = getClass().getResourceAsStream(
				"/testResponse2Client.xml");
		
		controladorDescarga.askgn2download(isRequestXML, "micho.garcia");
	}
}
