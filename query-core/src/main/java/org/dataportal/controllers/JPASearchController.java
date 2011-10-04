/**
 * 
 */
package org.dataportal.controllers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.dataportal.model.Search;

/**
 * @author michogar
 * 
 */
public class JPASearchController {

	EntityManagerFactory entityFactory = Persistence
			.createEntityManagerFactory("dataportal");

	/**
	 * 
	 * Create an entitymanager
	 * 
	 * @return EntityManager
	 */
	public EntityManager getEntityManager() {
		return entityFactory.createEntityManager();
	}

	/**
	 * JPASearchController Constructor
	 */
	public JPASearchController() {
	}

	/**
	 * Insert the search received in params into RDBMS
	 * 
	 * @param search
	 * @return boolean with operations result
	 */
	public boolean insert(Search search) {

		boolean inserted = false;
		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			manager.persist(search);
			transaction.commit();
			inserted = true;
		} catch (Exception e) {
			e.printStackTrace();
			inserted = false;
		} finally {
			if (manager != null)
				manager.close();
		}
		return inserted;
	}
}
