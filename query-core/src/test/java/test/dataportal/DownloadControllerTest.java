/**
 * 
 */
package test.dataportal;

import java.util.ArrayList;

import org.dataportal.DownloadController;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 *
 */
public class DownloadControllerTest extends TestCase {
	
	private ArrayList<String> arrayURLs;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		arrayURLs = new ArrayList<String>();
		//arrayURLs.add("http://thredds.daac.ornl.gov/thredds/fileServer/720/a784mfd.nc");
		//arrayURLs.add("http://ciclope.cmima.csic.es:8080/thredds/fileServer/testEnhanced/2004050400_eta_211.nc");
		//arrayURLs.add("http://ciclope.cmima.csic.es:8080/thredds/fileServer/testEnhanced/2004050312_eta_211.nc");
		arrayURLs.add("http://ciclope.cmima.csic.es:8080/thredds/fileServer/testDP/ocean-metadata-sample.nc");
	}

	/**
	 * Test method for {@link org.dataportal.DownloadController#downloadDatasets(java.util.ArrayList)}.
	 */
	public void testDownloadDatasets() {
		
		try {
			String finalizado = DownloadController.downloadDatasets(arrayURLs);
			System.out.println(finalizado);
			assertEquals("FINALIZADO", finalizado);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
