/**
 * 
 */
package test.dataportal.controllers;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dataportal.controllers.JPASearchController;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.model.Search;
import org.dataportal.model.User;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 *
 */
public class JPASearchControllerTest extends TestCase {

	private JPASearchController controladorBusqueda = new JPASearchController();
	private JPAUserController controladorUsuario = new JPAUserController();
	private User user = null;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		user = new User("un.correo@un.dominio.com", "unapassword");
		user.setState(JPAUserController.ACTIVE);
		controladorUsuario.insert(user);
	}

	/**
	 * Check if object exists into RDBMS and returns
	 * 
	 * @param object ID
	 * @return object record or null
	 */
	private Search exists(Integer id, Class<Search> clase) {
		EntityManager manager = getEntityManager();
		Search search = manager.find(clase, id);
		manager.close();
		if (search != null)
			return search;
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
	 * {@link org.dataportal.controllers.JPADownloadController#insert(Search)}
	 * .
	 * @throws Exception 
	 */
	public void testInsert() throws Exception {

		Search search = new Search();
		search.setText("oceans");
		search.setUserBean(user);
		search.setVariables("una,dos,tres,cuatro,cinco");
		search.setBboxes("[[0,0 90,90],[10,10 90,90]]");
		Date startDate = new Date(Calendar.getInstance().getTimeInMillis());
		search.setStartDate(startDate);
		Date endDate = new Date(Calendar.getInstance().getTimeInMillis());
		search.setEndDate(endDate);
		
		Integer id = controladorBusqueda.insert(search);
		Search oneMoreTimeSearch = exists(id, Search.class);
		assertNotNull(oneMoreTimeSearch);
		
		controladorBusqueda.delete(oneMoreTimeSearch);
		oneMoreTimeSearch = exists(id, Search.class);
		assertNull(oneMoreTimeSearch);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		controladorUsuario.delete(user);
		super.tearDown();
	}
	
	
	
}
