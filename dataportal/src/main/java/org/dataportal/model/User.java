package org.dataportal.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name="users")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String hash;

	private String password;

	private String state;

	//bi-directional many-to-one association to Download
	@OneToMany(mappedBy="userBean")
	private Set<Download> downloads;

	//bi-directional many-to-one association to Search
	@OneToMany(mappedBy="userBean")
	private Set<Search> searches;

    public User() {
    }
    
    public User(String id) {
        this.id = id;
    }
    
    public User(String id, String password) {
        this.id = id;
        this.password = password;
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

	public Set<Download> getDownloads() {
		return this.downloads;
	}

	public void setDownloads(Set<Download> downloads) {
		this.downloads = downloads;
	}
	
	public Set<Search> getSearches() {
		return this.searches;
	}

	public void setSearches(Set<Search> searches) {
		this.searches = searches;
	}
	
}