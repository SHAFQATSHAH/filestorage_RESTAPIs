package org.metadata.filestorage.pojo;

public class ProcessedFile {

	private String fileName;
	private Boolean uploaded;
	private String message;

	public ProcessedFile() {
		super();
	}

	public ProcessedFile(String fileName, Boolean uploaded, String message) {
		super();
		this.fileName = fileName;
		this.uploaded = uploaded;
		this.message = message;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getUploaded() {
		return uploaded;
	}

	public void setUploaded(Boolean uploaded) {
		this.uploaded = uploaded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
