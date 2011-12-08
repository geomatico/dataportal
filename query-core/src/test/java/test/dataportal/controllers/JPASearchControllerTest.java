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
import org.dataportal.model.Download;
import org.dataportal.model.Search;
import org.dataportal.model.User;

import junit.framework.TestCase;

/**
 * @author michogar
 *
 */
public class JPASearchControllerTest extends TestCase {

	private JPASearchController controladorBusqueda = null;
	private User user = null;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		controladorBusqueda = new JPASearchController();
		user = (User) exists("micho.garcia@geomati.co", User.class);
	}

	/**
	 * Check if object exists into RDBMS and returns
	 * 
	 * @param object ID
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

		Search search = new Search();
		search.setText("oceans");
		search.setUserBean(user);
		search.setVariables("una,dos,tres,cuatro,cinco");
		search.setBboxes("[[0,0 90,90],[10,10 90,90]]");
		Date startDate = new Date(Calendar.getInstance().getTimeInMillis());
		search.setStartDate(startDate);
		Date endDate = new Date(Calendar.getInstance().getTimeInMillis());
		search.setEndDate(endDate);
		
		controladorBusqueda.insert(search);
	}
}
