package org.metadata.filestorage.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.metadata.filestorage.common.FileStorageConstants;
import org.metadata.filestorage.jpa.BinaryFileEntity;
import org.metadata.filestorage.jpa.FileStorageRepo;
import org.metadata.filestorage.pojo.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Qualifier(FileStorageConstants.DB_FILE_STORAGE_SERVICE)
public class DBFileStorageService implements FileStorageService {

	@Autowired
	FileStorageRepo fileStorageRepo;

	public void addFile(MultipartFile file) throws Exception {
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		if (originalFileName.contains("..")) {
			throw new IllegalArgumentException(String.format("Invalid file name '%s'", originalFileName));
		}
		String fileName = new String(originalFileName);
		if (fileName.contains(File.pathSeparator)) {
			fileName = Paths.get(fileName).getFileName().toString();
		}
		BinaryFileEntity fileEntity = fileStorageRepo.findFirstByFileNameOrderByFileVersionDesc(fileName);
		if (fileEntity != null) {
			throw new FileAlreadyExistsException(String.format("'%s' aleady exsists", fileName));
		}
		BinaryFileEntity newFileEntity = new BinaryFileEntity();
		newFileEntity.setFileName(fileName);
		newFileEntity.setFileVersion(1);
		newFileEntity.setFileContent(ArrayUtils.toObject(file.getBytes()));
		fileStorageRepo.save(newFileEntity);
	}

	public void updateFile(MultipartFile file, Boolean overwrite) throws Exception {
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		if (originalFileName.contains("..")) {
			throw new IllegalArgumentException(String.format("Invalid file name '%s'", originalFileName));
		}
		String fileName = new String(originalFileName);
		if (fileName.contains(File.pathSeparator)) {
			fileName = Paths.get(fileName).getFileName().toString();
		}
		BinaryFileEntity fileEntity = fileStorageRepo.findFirstByFileNameOrderByFileVersionDesc(fileName);
		if (fileEntity == null) {
			throw new FileNotFoundException(String.format("'%s' doesn't exsist", fileName));
		}
		if (Boolean.TRUE.equals(overwrite)) {
			fileEntity.setFileContent(ArrayUtils.toObject(file.getBytes()));
			fileEntity.setFileVersion(fileEntity.getFileVersion() + 1);
			fileStorageRepo.save(fileEntity);
		} else {
			BinaryFileEntity newFileEntity = new BinaryFileEntity();
			newFileEntity.setFileName(fileName);
			newFileEntity.setFileVersion(fileEntity.getFileVersion() + 1);
			newFileEntity.setFileContent(ArrayUtils.toObject(file.getBytes()));
			fileStorageRepo.save(newFileEntity);
		}
	}

	@Transactional
	public void deleteFile(String fileName) throws Exception {
		if (fileName == null || fileName.isBlank()) {
			throw new IllegalArgumentException(String.format("Invalid file name '%s'", fileName));
		}
		BinaryFileEntity fileEntity = fileStorageRepo.findFirstByFileNameOrderByFileVersionDesc(fileName);
		if (fileEntity == null) {
			throw new FileNotFoundException(String.format("'%s' doesn't exsist", fileName));
		}
		fileStorageRepo.deleteByFileName(fileName);
	}

	public List<FileDTO> getAllFiles() throws Exception {
		List<BinaryFileEntity> files = fileStorageRepo.findAllFiles();
		if (files == null || files.isEmpty()) {
			throw new FileNotFoundException("No file found");
		}
		List<FileDTO> fileDTOs = new ArrayList<FileDTO>(files.size());
		files.stream().forEach(file -> {
			fileDTOs.add(new FileDTO(file.getFileName(), file.getFileVersion()));
		});
		return fileDTOs;
	}

	public Byte[] getFileContent(String fileName, Integer fileVersion) throws Exception {
		BinaryFileEntity binaryFileEntity = null;
		if (fileName == null || fileName.isBlank()) {
			throw new IllegalArgumentException(String.format("Invalid file name '%s'", fileName));
		}
		if (fileVersion != null && fileVersion.intValue() > 0) {
			binaryFileEntity = fileStorageRepo.findFileByNameAndVersion(fileName, fileVersion);
		} else {
			binaryFileEntity = fileStorageRepo.findFirstByFileNameOrderByFileVersionDesc(fileName);
		}
		if (binaryFileEntity == null) {
			throw new FileNotFoundException(String.format("'%s' doesn't exsist", fileName));
		}
		return binaryFileEntity.getFileContent();
	}

}
