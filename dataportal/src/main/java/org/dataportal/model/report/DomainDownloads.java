package org.dataportal.model.report;

public class DomainDownloads {
    private String domain;
    private Long downloads;

    public DomainDownloads() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

}
