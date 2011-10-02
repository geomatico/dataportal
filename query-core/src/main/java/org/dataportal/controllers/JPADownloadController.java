/**
 * 
 */
package org.dataportal.controllers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.dataportal.model.Download;

/**
 * 
 * Controller class to Downloads
 * 
 * @author Micho Garcia
 * 
 */
public class JPADownloadController {

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
	 * JPADownloadController Constructor
	 */
	public JPADownloadController() {
	}

	/**
	 * Insert the download received in params into RDBMS
	 * 
	 * @param download
	 * @return boolean with operations result
	 */
	public boolean insert(Download download) {

		boolean inserted = false;
		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			manager.persist(download);
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
	 * 
	 * Delete the download received in params from RDBMS
	 * 
	 * @param download
	 * @return boolean with operations result
	 */
	public boolean delete(Download download) {

		boolean deleted = false;
		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			Download downloadToRemove = manager.find(Download.class,
					download.getId());
			if (downloadToRemove != null)
				manager.remove(downloadToRemove);
			// TODO Something if else
			transaction.commit();
			deleted = true;
		} catch (Exception e) {
			e.printStackTrace();
			deleted = false;
		} finally {
			if (manager != null)
				manager.close();
		}
		return deleted;
	}

	/**
	 * 
	 * Check if download received in params exits into RDBMS
	 * 
	 * @param download
	 * @return Download records or null if record not exits
	 */
	public Download exits(Download download) {

		Download downloadInto = null;
		EntityManager manager = getEntityManager();
		downloadInto = manager.find(Download.class, download.getId());
		manager.close();

		return downloadInto;
	}

}
