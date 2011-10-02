/**
 * 
 */
package test.dataportal.controllers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dataportal.controllers.JPADownloadController;
import org.dataportal.model.Download;
import org.dataportal.model.User;
import org.dataportal.utils.Utils;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class JPADownloadControllerTest extends TestCase {

	private JPADownloadController controladorDescarga;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		controladorDescarga = new JPADownloadController();

	}

	/**
	 * Check if User exits into RDBMS and returns
	 * 
	 * @param idUser
	 * @return User record or null
	 */
	private User exits(String idUser) {
		EntityManager manager = getEntityManager();
		User user = manager.find(User.class, idUser);
		manager.close();
		if (user != null)
			return user;
		else
			return null;
	}

	/**
	 * Create an EntityManager
	 */
	public EntityManager getEntityManager() {
		EntityManagerFactory factoria = Persistence
				.createEntityManagerFactory("dataportalTest");
		EntityManager manager = factoria.createEntityManager();
		return manager;
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#insert(Download)}.
	 */
	public void testInsert() {
		User user = exits("micho.garcia");

		String idDownload = "10.4324/4234";
		Download download = new Download(idDownload,
				"micho.garcia_20110509.zip",
				Utils.extractDateSystemTimeStamp(), user);
		boolean insertado = controladorDescarga.insert(download);
		Download downloadInsertada = controladorDescarga.exits(download);
		boolean insertada = false;
		if (downloadInsertada != null)
			insertada = true;

		assertTrue(insertado);
		assertTrue(insertada);
	}
	
	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#delete(Download)}.
	 */
	public void testDelete() {
		Download download = new Download("10.4324/4234");
		Download downloadToRemove = controladorDescarga.exits(download);
		boolean borrada = false;
		if (downloadToRemove != null)
			borrada = true;
		controladorDescarga.delete(downloadToRemove);
		downloadToRemove = controladorDescarga.exits(download);

		assertTrue(borrada);
		assertEquals(downloadToRemove, null);
	}

}
