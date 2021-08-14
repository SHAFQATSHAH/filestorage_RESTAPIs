package org.metadata.filestorage.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileStorageRepo extends JpaRepository<BinaryFileEntity, Long> {

	@Query("SELECT fileEntity FROM BinaryFileEntity fileEntity ORDER BY lower(fileEntity.fileName) DESC")
	List<BinaryFileEntity> findAllFiles();

	BinaryFileEntity findFirstByFileNameOrderByFileVersionDesc(String fileName);

	@Query("SELECT fileEntity FROM BinaryFileEntity fileEntity WHERE fileEntity.fileName = ?1 AND fileEntity.fileVersion = ?2")
	BinaryFileEntity findFileByNameAndVersion(String fileName, Integer fileVersion);

	void deleteByFileName(String fileName);

}