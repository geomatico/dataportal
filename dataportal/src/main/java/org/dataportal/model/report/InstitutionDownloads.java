package org.dataportal.model.report;

public class InstitutionDownloads {
    private String institution;
    private Long downloads;

    public InstitutionDownloads() {
    }
    
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

}
