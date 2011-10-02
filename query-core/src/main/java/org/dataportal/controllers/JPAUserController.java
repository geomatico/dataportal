/**
 * 
 */
package org.dataportal.controllers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.dataportal.model.Download;
import org.dataportal.model.User;

/**
 * @author michogar
 *
 */
public class JPAUserController {
	
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
	 * @param user
	 * @return
	 */
	public boolean insert(User user) {
		
		boolean inserted = false;
		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			manager.persist(user);
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

	/**
	 * @param user
	 * @return
	 */
	public User exits(User user) {
		
		User userInto = null;
		EntityManager manager = getEntityManager();
		userInto = manager.find(User.class, user.getId());
		manager.close();

		return userInto;
	}

}
