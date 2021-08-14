package org.metadata.filestorage.pojo;

public class FileDTO {

	private String fileName;
	private Integer fileVersion;

	public FileDTO() {
		super();
	}

	public FileDTO(String fileName, Integer fileVersion) {
		super();
		this.fileName = fileName;
		this.fileVersion = fileVersion;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
	}

}
