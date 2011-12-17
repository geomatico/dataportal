/**
 * 
 */
package test.dataportal;

import java.io.InputStream;

import org.dataportal.DownloadController;
import org.dataportal.controllers.JPADownloadController;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.model.User;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class DownloadControllerTest extends TestCase {

	private DownloadController controladorDescarga;
	private JPAUserController controladorUsuario = new JPAUserController();
	private JPADownloadController controladorDownload = new JPADownloadController();
	User user;
	
	boolean insertado = true;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {		
		
		controladorDescarga = new DownloadController();
		
		user = new User("user.test@mail.com", "password.test");
		
		if (controladorUsuario.existsInto(user) == null) {			
			user.setState("ACTIVE");			
			insertado = controladorUsuario.insert(user);			
		}
		
		super.setUp();
	}

	/**
	 * Test method for
	 * {@link org.dataportal.DownloadController#askgn2Download(java.util.InputStream)}
	 * .
	 */
	public void testAskgn2download() throws Exception {
		
		InputStream isRequestXML = getClass().getResourceAsStream(
				"/testResponse2Client.xml");
		
		// TODO Hacer que elimine los tests de la base de datos IMPORTANTE
		if (insertado)
			controladorDescarga.askgn2download(isRequestXML, user.getId());
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
			
		super.tearDown();
	}
	
	
}
