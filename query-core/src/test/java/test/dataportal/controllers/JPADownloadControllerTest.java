/**
 * 
 */
package test.dataportal.controllers;

import java.util.ArrayList;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dataportal.controllers.JPADownloadController;
import org.dataportal.model.Download;
import org.dataportal.model.DownloadItem;
import org.dataportal.model.User;
import org.dataportal.utils.Utils;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class JPADownloadControllerTest extends TestCase {

	private JPADownloadController controladorDescarga;
	private User user = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		controladorDescarga = new JPADownloadController();
		user = (User) exists("micho.garcia", User.class);
	}

	/**
	 * Check if object exists into RDBMS and returns
	 * 
	 * @param object
	 *            ID
	 * @return object record or null
	 */
	@SuppressWarnings("unchecked")
	private Object exists(String id, Class clase) {
		EntityManager manager = getEntityManager();
		Object objeto = manager.find(clase, id);
		manager.close();
		if (objeto != null)
			return objeto;
		else
			return null;
	}

	/**
	 * Create an EntityManager
	 */
	public EntityManager getEntityManager() {
		EntityManagerFactory factoria = Persistence
				.createEntityManagerFactory("dataportal");
		EntityManager manager = factoria.createEntityManager();
		return manager;
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#insert(Download)}
	 * .
	 */
	public void testInsert() {

		String idDownload = "10.4324/4234";
		Download download = new Download(idDownload,
				"micho.garcia_20110509.zip",
				Utils.extractDateSystemTimeStamp(), user);
		boolean insertado = controladorDescarga.insert(download);
		try {
			Thread.sleep(9000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Download downloadInsertada = controladorDescarga.exists(download);
		boolean insertada = false;
		if (downloadInsertada != null)
			insertada = true;

		assertTrue(insertado);
		assertTrue(insertada);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#insert(Download)}
	 * .
	 */
	public void testInsertItems() {

		String idDownload = UUID.randomUUID().toString();
		Download download = new Download(idDownload,
				"micho.garcia_20110509.zip",
				Utils.extractDateSystemTimeStamp(), user);

		ArrayList<DownloadItem> items = new ArrayList<DownloadItem>();
		DownloadItem item1 = new DownloadItem();
	    item1.setItemId(UUID.randomUUID().toString());
	    item1.setUrl("un nombre de archivo");
		item1.setDownloadBean(download);
		items.add(item1);
		
		DownloadItem item2 = new DownloadItem();
        item2.setItemId(UUID.randomUUID().toString());
		item2.setUrl("otro nombre de un archivo");
        item2.setDownloadBean(download);
		items.add(item2);
		
		DownloadItem item3 = new DownloadItem();
        item3.setItemId(UUID.randomUUID().toString());
        item3.setUrl("el ultimo nombre de archivo");
        item3.setDownloadBean(download);
		items.add(item3);

		boolean insertado = controladorDescarga.insertItems(download, items);
		assertTrue(insertado);

		Download insertada = (Download) exists(idDownload, Download.class);
		assertNotSame(insertada, null);

		controladorDescarga.delete(download);

		insertada = (Download) exists(idDownload, Download.class);
		assertEquals(insertada, null);

	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#delete(Download)}
	 * .
	 */
	public void testDelete() {
		Download download = new Download("10.4324/4234");
		Download downloadToRemove = controladorDescarga.exists(download);
		boolean borrada = false;
		if (downloadToRemove != null)
			borrada = true;
		controladorDescarga.delete(downloadToRemove);
		downloadToRemove = controladorDescarga.exists(download);

		assertTrue(borrada);
		assertEquals(downloadToRemove, null);
	}

}
