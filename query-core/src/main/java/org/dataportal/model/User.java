package org.dataportal.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name="users")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=128)
	private String id;

	@Column(length=32)
	private String hash;

	@Column(nullable=false, length=32)
	private String password;

	@Column(nullable=false, length=16)
	private String state;

	//bi-directional many-to-one association to Download
	@OneToMany(mappedBy="userBean")
	private List<Download> downloads;

	//bi-directional many-to-one association to Search
	@OneToMany(mappedBy="userBean")
	private List<Search> searches;

    public User() {
    }
    
    public User(String id) {
    	this.id = id;
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<Download> getDownloads() {
		return this.downloads;
	}

	public void setDownloads(List<Download> downloads) {
		this.downloads = downloads;
	}
	
	public List<Search> getSearches() {
		return this.searches;
	}

	public void setSearches(List<Search> searches) {
		this.searches = searches;
	}
	
}