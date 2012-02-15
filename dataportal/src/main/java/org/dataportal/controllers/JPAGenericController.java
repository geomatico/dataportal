package org.dataportal.controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.*;

import org.dataportal.utils.DataPortalException;

public class JPAGenericController {

    private EntityManagerFactory entityFactory;
    
    public JPAGenericController(String persistenceUnit) {
        this.entityFactory = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List select(String jpqlQuery, Map<String, Object> queryParams, Class returnClass) throws DataPortalException {
        List data = null;
        EntityManager entityManager = this.entityFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            TypedQuery query = entityManager.createQuery(jpqlQuery, returnClass);
            for (Entry<String, Object> param : queryParams.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
            data = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataPortalException(DataPortalException.RDBMSERROR, e);
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return data;
    }
    
}
