package org.metadata.filestorage.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.sun.istack.NotNull;

@Entity
@Table(name = "binary_file")
public class BinaryFileEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "file_id")
	private Long fileId;

	@NotNull
	@Column(name = "file_name")
	private String fileName;

	@Lob
	@NotNull
	@Column(name = "file_content")
	@Basic(fetch = FetchType.LAZY)
	private Byte[] fileContent;

	@NotNull
	@Column(name = "file_version")
	private Integer fileVersion;

	public BinaryFileEntity() {
		super();
	}

	public BinaryFileEntity(String fileName, Byte[] fileContent, Integer fileVersion) {
		super();
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.fileVersion = fileVersion;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(Byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public Integer getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
	}

}