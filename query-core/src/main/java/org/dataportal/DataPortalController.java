/**
 * 
 */
package org.dataportal;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.dataportal.controllers.JPADownloadController;
import org.dataportal.controllers.JPASearchController;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.csw.Catalog;
import org.dataportal.csw.DataPortalNS;
import org.dataportal.model.User;
import org.dataportal.utils.DataPortalException;

/**
 * @author Micho Garcia
 * 
 */
public class DataPortalController implements DataportalCodes {

	private static Logger logger = Logger.getLogger(DataPortalController.class);

	protected static Catalog catalogo;
	protected DataPortalException dtException;
	protected JPAUserController userJPAController;
	protected JPADownloadController downloadJPAController;
	protected JPASearchController searchJPAController;
	protected DataPortalNS dataPortalCtx = new DataPortalNS();
	protected User user;

	/**
	 * @throws MalformedURLException
	 */
	public DataPortalController() throws MalformedURLException {
		
		userJPAController = new JPAUserController();

		String url = Config.get("csw.url");
		catalogo = new Catalog(url);
		logger.debug("Catalog created with URL: " + url);
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = userJPAController.existsInto(user);
	}
	
	
}
