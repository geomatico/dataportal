/**
 * 
 */
package test.dataportal.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dataportal.controllers.JPAUserController;
import org.dataportal.model.User;

import junit.framework.TestCase;

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
		user = new User("un.correo@un.dominio.com", "unapassword");
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
	 */
	public void testSave() {
		String hash = null;
		hash = controladorUsuario.save(user);
		assertNotNull(hash);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#getState()}.
	 */
	public void testGetState() {
		String state = null;
		User userInto = controladorUsuario.exitsInto(user);
		state = controladorUsuario.getState(userInto);
		assertEquals(state, "NOT_CONFIRMED");
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#activate(java.lang.String)}
	 * .
	 */
	public void testActivate() {
		String id = null;
		User userInto = controladorUsuario.exitsInto(user);
		id = controladorUsuario.activate(userInto.getHash());
		assertEquals(user.getId(), id);
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
	 */
	public void testSetHash() {
		String hash = null;
		hash = controladorUsuario.setHash(user);
		assertNotNull(hash);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#newPass(java.lang.String)}
	 * .
	 */
	public void testNewPass() {
		String newPass = null;
		User userInto = controladorUsuario.exitsInto(user);
		newPass = controladorUsuario.newPass(userInto.getHash());
		assertNotNull(newPass);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#exists()}.
	 */
	public void testExists() {
		boolean exits = true;
		exits = controladorUsuario.exits(user);
		assertFalse(exits);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#changePass(java.lang.String)}
	 * .
	 */
	public void testChangePass() {
		String newPassword = hex_md5("va a ser otra mas");
		User userInto = controladorUsuario.exitsInto(user);
		boolean changed = controladorUsuario.changePass(userInto, newPassword);
		assertTrue(changed);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#changeState(int, java.lang.String)}
	 * .
	 */
	public void testChangeState() {
		String state = "ACTIVE";
		user.setState(state);
		controladorUsuario.changeState(user);
		User userInto = controladorUsuario.exitsInto(user);
		assertEquals(userInto.getState(), state);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.controllers.JPAUserController#delete(int)}.
	 */
	public void testDelete() {
		controladorUsuario.delete(user);
		User userInto = controladorUsuario.exitsInto(user);
		assertNull(userInto);
	}
}
