package org.dataportal;

import java.util.*;

import org.dataportal.model.report.*;
import org.dataportal.utils.DataPortalException;
import org.dataportal.controllers.JPAGenericController;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ReportController {
    
    private JPAGenericController database;
    
    public ReportController() {
        this.database = new JPAGenericController("dataportal");
    }
    
    public List get(String request, int year, int month) throws DataPortalException {
        
        Map<String, Integer> requestParams = new HashMap();
        requestParams.put("year", year);
        if (month > 0 && month <= 12) {
            requestParams.put("month", month-1);
        }
        
        if (request.equalsIgnoreCase("GetDownloadsByInstitution")) {
            return getDownloadsByInstitution(requestParams);
        } else if (request.equalsIgnoreCase("GetDownloadsByDomain")) {
            return getDownloadsByDomain(requestParams);
        } else if (request.equalsIgnoreCase("GetDownloadsByDate")) {
            return getDownloadsByDate(requestParams);
        } else {
            throw new DataPortalException(DataPortalException.INVALIDREQUEST);
        }
    }
    
    private List getDownloadsByInstitution(Map params) throws DataPortalException {
        String jpql = "SELECT item.institution as institution, count(item.institution) as downloads FROM DownloadItem item JOIN item.downloadBean download WHERE year(download.timestamp) = :year";
        if (params.containsKey("month")) {
            jpql += " AND month(download.timestamp) = :month";
        }
        jpql += " GROUP BY item.institution";
        return this.database.select(jpql, params, InstitutionDownloads.class);
    }

    private List getDownloadsByDomain(Map params) throws DataPortalException {
        String jpql = "SELECT item.icosDomain as domain, count(item.icosDomain) as downloads FROM DownloadItem item JOIN item.downloadBean download WHERE year(download.timestamp) = :year";
        if (params.containsKey("month")) {
            jpql += " AND month(download.timestamp) = :month";
        }
        jpql += " GROUP BY icosDomain";
        return this.database.select(jpql, params, DomainDownloads.class);
    }
    
    private List getDownloadsByDate(Map params) throws DataPortalException {
        String jpql;
        if (params.containsKey("month")) {
            jpql  = " SELECT day(download.timestamp) as date, count(day(download.timestamp)) as downloads FROM Download download";
            jpql += " WHERE year(download.timestamp) = :year AND month(download.timestamp) = :month";
            jpql += " GROUP BY day(download.timestamp) ORDER BY day(download.timestamp) asc";
        } else {
            jpql  = " SELECT month(download.timestamp)+1 as date, count(month(download.timestamp)) as downloads FROM Download download";
            jpql += " WHERE year(download.timestamp) = :year";
            jpql += " GROUP BY month(download.timestamp) ORDER BY month(download.timestamp) asc";            
        }
        return this.database.select(jpql, params, DateDownloads.class);
    }
    
}
