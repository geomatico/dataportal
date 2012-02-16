/**
 * 
 */
package test.dataportal.controllers;

import java.util.ArrayList;
import java.util.UUID;

import org.dataportal.controllers.JPADownloadController;
import org.dataportal.controllers.JPAUserController;
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

	private JPADownloadController controladorDescarga = new JPADownloadController();
	private JPAUserController controladorUsuario = new JPAUserController();
	private User user = null;
	static final String IDDOWNLOADTEST = "00000000-0000-0000-0000-000000000000";

	private void createUser() throws Exception {
		user = controladorUsuario.existsInto(new User("user.test"));
		if ( user == null) {
			user = new User("user.test", "password.test");
			user.setState(JPAUserController.ACTIVE);
			controladorUsuario.insert(user);
		}
	}
	
	public void testDownloads() throws Exception {
		// This is a hack to force an ordered execution... shouldn't.
		insert();
		insertItems();
		getDownloadItems();
		delete();
		
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#insert(Download)}
	 * .
	 * @throws Exception 
	 */
	public void insert() throws Exception {
		
		createUser();
		
		Download download = new Download(IDDOWNLOADTEST,
				"micho.garcia_20110509.zip",
				Utils.extractDateSystemTimeStamp(), user);
		
		// Just remove it if it already exists.
		Download downloadToRemove = controladorDescarga.exists(download);
		if (downloadToRemove != null) {
			controladorDescarga.delete(downloadToRemove);
		}
		
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
	 * @throws Exception 
	 */
	public void insertItems() throws Exception {

		Download download = controladorDescarga.exists(new Download(IDDOWNLOADTEST));
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

		Download insertada = controladorDescarga.exists(download);
		assertNotNull(insertada);

	}
	
	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#getDownloadItems(Download)}
	 * .
	 * @throws Exception
	 */
	public void getDownloadItems() throws Exception {
		Download download = controladorDescarga.exists(new Download(IDDOWNLOADTEST));
		ArrayList<DownloadItem> items = controladorDescarga.getDownloadItems(download);
		assertEquals(3, items.size());
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPADownloadController#delete(Download)}
	 * .
	 * @throws Exception 
	 */
	public void delete() throws Exception {
		Download download = new Download(IDDOWNLOADTEST);
		Download downloadToRemove = controladorDescarga.exists(download);
		boolean borrada = false;
		if (downloadToRemove != null)
			borrada = true;
		controladorDescarga.delete(downloadToRemove);
		downloadToRemove = controladorDescarga.exists(download);

		assertTrue(borrada);
		assertNull(downloadToRemove);
		
		user = new User("user.test");
		if (controladorUsuario.existsInto(user) != null)
			controladorUsuario.delete(user);
	}
}
