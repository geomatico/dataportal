package org.dataportal.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the download database table.
 * 
 */
@Entity
@Table(name="download")
public class Download implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=128)
	private String id;

	@Column(length=2147483647)
	private String filename;

	@Column(nullable=false)
	private Timestamp timestamp;

	//bi-directional many-to-one association to User
    @ManyToOne
	@JoinColumn(name="user")
	private User userBean;

	//bi-directional many-to-one association to DownloadItem
	@OneToMany(mappedBy="download")
	private List<DownloadItem> downloadItems;

    public Download() {
    }
    
    public Download(String id) {
    	this.id = id;
    }
    
    public Download(String id, String filename, Timestamp timestamp, User user) {
    	this.id = id;
    	this.filename = filename;
    	this.timestamp = timestamp;
    	this.userBean = user;
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public User getUserBean() {
		return this.userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}
	
	public List<DownloadItem> getDownloadItems() {
		return this.downloadItems;
	}

	public void setDownloadItems(List<DownloadItem> downloadItems) {
		this.downloadItems = downloadItems;
	}
	
}