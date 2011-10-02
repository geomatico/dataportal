/**
 * 
 */
package test.dataportal.controllers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dataportal.controllers.JPAUserController;
import org.dataportal.model.Download;
import org.dataportal.model.User;
import org.dataportal.utils.Utils;

import junit.framework.TestCase;

/**
 * @author michogar
 *
 */
public class JPAUserControllerTest extends TestCase {
	
	private JPAUserController controladorUsuario = null;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		controladorUsuario = new JPAUserController();
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
	
	public void testExits() {
		String idUser = "micho.garcia";
		User user = new User(idUser);
		User userInsertado = controladorUsuario.exits(user);
		boolean comprobado = false;
		if (userInsertado != null)
			comprobado = true;

		assertTrue(comprobado);
	}

}
