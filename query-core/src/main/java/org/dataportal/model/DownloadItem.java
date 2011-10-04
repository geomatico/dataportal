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

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id_item", unique=true, nullable=false)
	private Integer idItem;

	@Column(nullable=false, length=2147483647)
	private String text;

	//bi-directional many-to-one association to Download
    @ManyToOne
	@JoinColumn(name="id_download", nullable=false)
	private Download download;

    public DownloadItem() {
    }
    
    public DownloadItem(String filename, Download download) {
    	this.text = filename;
    	this.download = download;
    }

	public DownloadItem(String filename) {
		this.text = filename;
	}

	public Integer getIdItem() {
		return this.idItem;
	}

	public void setIdItem(Integer idItem) {
		this.idItem = idItem;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Download getDownload() {
		return this.download;
	}

	public void setDownload(Download download) {
		this.download = download;
	}
	
}