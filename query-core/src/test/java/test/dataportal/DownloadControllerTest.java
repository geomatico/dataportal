/**
 * 
 */
package test.dataportal;

import java.util.ArrayList;

import org.dataportal.DownloadController;
import org.dataportal.utils.Utils;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class DownloadControllerTest extends TestCase {

	private ArrayList<String> arrayURLs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		arrayURLs = new ArrayList<String>();
		//arrayURLs
				//.add("http://thredds.daac.ornl.gov/thredds/fileServer/720/a784mfd.nc");
		//arrayURLs
				//.add("http://thredds.daac.ornl.gov/thredds/fileServer/811/Hyytiala/Hyytiala_96_Daily.nc");
		arrayURLs
				.add("http://ciclope.cmima.csic.es:8080/thredds/fileServer/testDP/ocean-metadata-sample.nc");
		arrayURLs
				.add("http://thredds.daac.ornl.gov/thredds/fileServer/811/Barrow/Barrow_98_Monthly_UCorrected.nc");
	}

	/**
	 * Test method for
	 * {@link org.dataportal.DownloadController#downloadDatasets(java.util.ArrayList)}
	 * .
	 */
	public void testDownloadDatasets() {

		try {
			DownloadController controladorDescarga = new DownloadController();
			String finalizado = controladorDescarga.downloadDatasets(arrayURLs, "admin");
			System.out.println(finalizado);
			// TODO cambiar la ruta de este test (solo vale para mi)
			assertEquals("/home/michogar/geomati.co/CMIMA/data/tmp/admin/admin_" + Utils.extractDateSystem() + ".tar", finalizado);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
