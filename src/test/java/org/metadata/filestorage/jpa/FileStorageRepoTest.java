package org.metadata.filestorage.jpa;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FileStorageRepoTest {

	@Autowired
	private FileStorageRepo repository;

	@Test
	public void testFileCreation() {
		Byte[] content = new Byte[] { 1, 1 };
		repository.save(new BinaryFileEntity("test_file_1.tmp", content, 1));
		BinaryFileEntity binaryFileEntity = repository.findFileByNameAndVersion("test_file_1.tmp", 1);
		assertNotNull(binaryFileEntity);
	}

	@Test
	public void testListFiles() {
		Byte[] content = new Byte[] { 1, 1 };
		repository.save(new BinaryFileEntity("test_file_2.tmp", content, 1));
		List<BinaryFileEntity> files = repository.findAllFiles();
		assertNotNull(files);
	}

	@Test
	public void testDeleteFile() {
		Byte[] content = new Byte[] { 1, 1 };
		repository.save(new BinaryFileEntity("test_file_3.tmp", content, 1));
		BinaryFileEntity binaryFileEntity = repository.findFileByNameAndVersion("test_file_3.tmp", 1);
		repository.deleteByFileName("test_file_3.tmp");
		binaryFileEntity = repository.findFirstByFileNameOrderByFileVersionDesc("test_file_3.tmp");
		assertNull(binaryFileEntity);
	}

}
