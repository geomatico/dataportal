/**
 * 
 */
package org.dataportal;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.dataportal.csw.Catalog;
import org.dataportal.utils.DataPortalException;

/**
 * @author Micho Garcia
 * 
 */
public class DataPortalController implements DataportalCodes {

	private static Logger logger = Logger.getLogger(DataPortalController.class);

	protected static Catalog catalogo;
	protected DataPortalException dtException;

	public DataPortalController() throws MalformedURLException {

		String url = Config.get("csw.url");
		catalogo = new Catalog(url);
		logger.debug("Catalog created with URL: " + url);
	}
}
