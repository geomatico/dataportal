package org.dataportal.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the search database table.
 * 
 */
@Entity
@Table(name="search")
public class Search implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false)
	private Integer id;

	private Array bboxes;

    @Temporal( TemporalType.DATE)
	@Column(name="end_date")
	private Date endDate;

    @Temporal( TemporalType.DATE)
	@Column(name="start_date")
	private Date startDate;

	@Column(length=2147483647)
	private String text;

	private Timestamp timestamp;

	@Column(length=2147483647)
	private Array variables;

	//bi-directional many-to-one association to User
    @ManyToOne
	@JoinColumn(name="user")
	private User userBean;

    public Search() {
    }

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Array getBboxes() {
		return this.bboxes;
	}

	public void setBboxes(Array bboxes) {
		this.bboxes = bboxes;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Array getVariables() {
		return this.variables;
	}

	public void setVariables(Array variables) {
		this.variables = variables;
	}

	public User getUserBean() {
		return this.userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}
	
}