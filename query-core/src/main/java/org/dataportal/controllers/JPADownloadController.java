/**
 * 
 */
package org.dataportal.controllers;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.dataportal.model.Download;
import org.dataportal.model.DownloadItem;

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
	 * @throws Exception 
	 */
	public boolean insert(Download download) throws Exception {

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
			throw e;
		} finally {
		    if (transaction.isActive())
		    {
		        transaction.rollback();
		    }
			if (manager != null)
				manager.close();
		}
		return inserted;
	}

	/**
	 * 
	 * Insert a download with all items into RDBMS
	 * 
	 * @param download
	 *            Download
	 * @param items
	 *            DownloadItem ArrayList
	 * @return
	 * @throws Exception 
	 */
	public boolean insertItems(Download download, ArrayList<DownloadItem> items) throws Exception {

		boolean inserted = false;
		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			if (exists(download) == null)
				manager.persist(download);
			for (DownloadItem item : items) {
				item.setDownloadBean(download);
				manager.persist(item);
			}
			transaction.commit();
			inserted = true;
		} catch (Exception e) {
			e.printStackTrace();
			inserted = false;
			throw e;
		} finally {
		    if (transaction.isActive())
		    {
		        transaction.rollback();
		    }
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
	 * @throws Exception 
	 */
	public boolean delete(Download download) throws Exception {
		boolean deleted = false;
		EntityManager manager = getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		try {
			transaction.begin();
			download = manager.find(Download.class, download.getId());
			if (download != null) {
	            for (DownloadItem item : download.getDownloadItems()) {
	                manager.remove(item);
	            }
			    manager.remove(download);
			} // else?
			transaction.commit();
			deleted = true;
		} catch (Exception e) {
			e.printStackTrace();
			deleted = false;
			throw e;
		} finally {
		    if (transaction.isActive()) {
		        transaction.rollback();
		    }
			if (manager != null) {
				manager.close();
			}
		}
		return deleted;
	}

	/**
	 * 
	 * Check if download received in params exists into RDBMS
	 * 
	 * @param download
	 * @return Download records or null if record not exists
	 */
	public Download exists(Download download) {

		Download downloadInto = null;
		EntityManager manager = getEntityManager();
		downloadInto = manager.find(Download.class, download.getId());
		manager.close();

		return downloadInto;
	}

}
