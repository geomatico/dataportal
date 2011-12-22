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
 * @author Micho Garcia
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
	 * @throws Exception 
	 */
	public Integer insert(Search search) throws Exception {

		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			manager.persist(search);
			transaction.commit();
			Integer id = search.getId();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		    if (transaction.isActive())
		    {
		        transaction.rollback();
		    }
			if (manager != null)
				manager.close();
		}
	}
	
	public void delete(Search search) throws Exception {
		
		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			Search searchInto = manager.find(Search.class, search.getId());
			if (searchInto != null) {
				manager.remove(searchInto);
				transaction.commit();
			} else {
				transaction.rollback();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			if (manager != null)
				manager.close();
		}
	}
}
