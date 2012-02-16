/**
 * 
 */
package test.dataportal;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dataportal.Config;
import org.dataportal.DownloadController;
import org.dataportal.controllers.JPADownloadController;
import org.dataportal.controllers.JPAGenericController;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.model.Download;
import org.dataportal.model.User;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class DownloadControllerTest extends TestCase {

	private JPAUserController controladorUsuario = new JPAUserController();
	private JPADownloadController controladorDescarga = new JPADownloadController();
	private JPAGenericController controladorGenerico = new JPAGenericController(
			"dataportal");

	private User user = null;
	private String tempDir;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		boolean inserted = false, exists = false;
		user = controladorUsuario.existsInto(new User("user.test"));
		if (user == null) {
			user = new User("user.test", "password.test");
			user.setState(JPAUserController.ACTIVE);
			inserted = controladorUsuario.insert(user);
			exists = (controladorUsuario.existsInto(user) != null);
			System.out.println(inserted);
			System.out.println(exists);
		}
		tempDir = Config.get("temp.dir");
	}

	/**
	 * Test method for
	 * {@link org.dataportal.DownloadController#askgn2Download(java.util.InputStream)}
	 * .
	 */
	public void testAskgn2download() throws Exception {

		InputStream isRequestXML = getClass().getResourceAsStream(
				"/testResponse2Client.xml");

		DownloadController controlador = new DownloadController("es");
		user = controladorUsuario.existsInto(user);
		controlador.setUser(user);
		controlador.setUrl("http://foo.com");
		String filename = controlador.askgn2download(isRequestXML);
		File file = FileUtils.getFile(tempDir + "/" + filename);
		assertNotNull(file);		
		FileUtils.deleteDirectory(new File(tempDir + "/" + user.getId()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		String jpql = "SELECT download FROM Download download WHERE download.userBean = :user";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		@SuppressWarnings("unchecked")
		List<Download> downloads = controladorGenerico.select(jpql, params,
				Download.class);
		for (Download descarga : downloads) {
			controladorDescarga.delete(descarga);
		}		
		controladorUsuario.delete(user);
	}

}
