package org.metadata.filestorage.service;

import java.util.List;

import org.metadata.filestorage.pojo.FileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	public void addFile(MultipartFile file) throws Exception;

	public void updateFile(MultipartFile file, Boolean overwrite) throws Exception;

	public void deleteFile(String fileName) throws Exception;

	public Byte[] getFileContent(String fileName, Integer fileVersion) throws Exception;

	public List<FileDTO> getAllFiles() throws Exception;

}
