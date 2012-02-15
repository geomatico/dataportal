package org.dataportal.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the download_item database table.
 * 
 */
@Entity
@Table(name="download_item")
public class DownloadItem implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="icos_domain")
	private String icosDomain;

	private String institution;

	@Column(name="item_id")
	private String itemId;

	private String url;

	//bi-directional many-to-one association to Download
    @ManyToOne
	@JoinColumn(name="download")
	private Download downloadBean;

    public DownloadItem() {
    }
    
    /*
    public DownloadItem(String url) {
        this.url = url;
    }
    */
    /*
    public DownloadItem(String url, Download download) {
        this.url = url;
        this.downloadBean = download;
    }
    */

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIcosDomain() {
		return this.icosDomain;
	}

	public void setIcosDomain(String icosDomain) {
		this.icosDomain = icosDomain;
	}

	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getItemId() {
		return this.itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Download getDownloadBean() {
		return this.downloadBean;
	}

	public void setDownloadBean(Download downloadBean) {
		this.downloadBean = downloadBean;
	}
	
}