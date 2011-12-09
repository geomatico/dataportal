/**
 * 
 */
package test.dataportal;

import java.io.InputStream;

import org.dataportal.DownloadController;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.model.User;
import org.dataportal.utils.DataPortalException;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class DownloadControllerTest extends TestCase {

	private DownloadController controladorDescarga = new DownloadController();
	private JPAUserController controladorUsuario = new JPAUserController();
	User user;
	
	boolean insertado = true;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {		
		
		user = new User("micho.garcia", "unapassword");
		
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
		
		if (insertado)
			controladorDescarga.askgn2download(isRequestXML, "micho.garcia");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		
		//controladorUsuario.delete(user);
		super.tearDown();
	}
	
	
}
