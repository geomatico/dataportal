/**
 * 
 */
package test.dataportal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	User user = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		user = controladorUsuario.existsInto(new User("user.test"));
		if (user == null) {
			user = new User("user.test", "password.test");
			user.setState(JPAUserController.ACTIVE);
			controladorUsuario.insert(user);
		}
	}

	/**
	 * Test method for
	 * {@link org.dataportal.DownloadController#askgn2Download(java.util.InputStream)}
	 * .
	 */
	public void testAskgn2download() throws Exception {

		InputStream isRequestXML = getClass().getResourceAsStream(
				"/testResponse2Client.xml");

		DownloadController controlador = new DownloadController();
		controlador.askgn2download(isRequestXML, user.getId());
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
