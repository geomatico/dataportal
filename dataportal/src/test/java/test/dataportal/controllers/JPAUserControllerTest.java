/**
 * 
 */
package test.dataportal.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.dataportal.SystemSingleton;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.model.User;

/**
 * @author michogar
 * 
 */
public class JPAUserControllerTest extends TestCase {

	private JPAUserController controladorUsuario = null;
	private User user = null;

	private static final char[] HEX = new String("0123456789abcdef")
			.toCharArray();

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		controladorUsuario = new JPAUserController();
		user = new User("user.test", "password.test");
	}

	/**
	 * Create an EntityManager
	 */
	public EntityManager getEntityManager() {
		EntityManagerFactory factoria = Persistence
				.createEntityManagerFactory(SystemSingleton
						.getPersistenceUnit());
		EntityManager manager = factoria.createEntityManager();
		return manager;
	}

	private String hex_md5(String stringToHash) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(stringToHash.getBytes());
			StringBuilder sb = new StringBuilder(2 * bytes.length);
			for (int i = 0; i < bytes.length; i++) {
				int low = (int) (bytes[i] & 0x0f);
				int high = (int) ((bytes[i] & 0xf0) >> 4);
				sb.append(HEX[high]);
				sb.append(HEX[low]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#save()}.
	 * @throws Exception 
	 */
	public void testSave() throws Exception {
		String hash = null;
		hash = controladorUsuario.save(user);
		User userInto = controladorUsuario.existsInto(user);
		assertNotNull(hash);
		assertNotNull(userInto);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#getState()}.
	 */
	public void testGetState() {
		String state = null;
		User userInto = controladorUsuario.existsInto(user);
		state = controladorUsuario.getState(userInto);
		assertEquals(state, JPAUserController.NOT_CONFIRMED);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#activate(java.lang.String)}
	 * .
	 * @throws Exception 
	 */
	public void testActivate() throws Exception {
		String id = null;
		User userInto = controladorUsuario.existsInto(user);
		id = controladorUsuario.activate(userInto.getHash());
		assertEquals(userInto.getId(), id);
	}
	
	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#isActive()}.
	 */
	public void testIsActive() {
		boolean activo = false;
		activo = controladorUsuario.isActive(user);
		assertTrue(activo);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#setHash(java.lang.String, java.lang.String)}
	 * .
	 * @throws Exception 
	 */
	public void testSetHash() throws Exception {
		String hash = null;
		hash = controladorUsuario.setHash(user, JPAUserController.ACTIVE);
		User userInto = controladorUsuario.existsInto(user);
		assertNotNull(hash);
		assertEquals(hash, userInto.getHash());
		assertEquals(JPAUserController.ACTIVE, userInto.getState());		
	}

    /**
     * Test method for
     * {@link org.dataportal.controllers.JPAUserController#setPass(java.lang.String, java.lang.String)}
     * .
     * 
     * @throws Exception
     */
    public void testSetPass() throws Exception {
		User userInto = controladorUsuario.existsInto(user);
        String newPass = controladorUsuario.createRandomPass(6);
        User userChanged = controladorUsuario.setPass(userInto.getHash(),
                newPass);
        assertNotNull(userChanged);
		userInto = controladorUsuario.existsInto(user);
        String hashedPass = hex_md5(userInto.getId() + ":" + newPass);
        assertEquals(hashedPass, userInto.getPassword());
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#exists()}.
	 */
	public void testExists() {
		boolean exists = false;
		exists = controladorUsuario.exists(user);
		assertTrue(exists);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#changePass(java.lang.String)}
	 * .
	 * @throws Exception 
	 */
	public void testChangePass() throws Exception {
		String newPassword = hex_md5("va a ser otra mas");
		User userInto = controladorUsuario.existsInto(user);
		boolean changed = controladorUsuario.changePass(userInto, newPassword);
		assertTrue(changed);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#changeState(int, java.lang.String)}
	 * .
	 * @throws Exception 
	 */
	public void testChangeState() throws Exception {
		String state = "ACTIVE";
		user.setState(state);
		controladorUsuario.changeState(user);
		User userInto = controladorUsuario.existsInto(user);
		assertEquals(userInto.getState(), state);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#delete(int)}.
	 * @throws Exception 
	 */
	public void testDelete() throws Exception {
		controladorUsuario.delete(user);
		User userInto = controladorUsuario.existsInto(user);
		assertNull(userInto);
	}
}
