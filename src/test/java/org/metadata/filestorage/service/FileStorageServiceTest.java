package org.metadata.filestorage.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.metadata.filestorage.pojo.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FileStorageServiceTest {

	@Autowired
	private DBFileStorageService fileStorageService;

	private List<String> fileNames;

	@BeforeEach
	private void init() {
		fileNames = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			fileNames.add(String.format("test_file_%s.tmp", i));
		}
	}

	@AfterEach
	private void cleanup() {
		for (String fileName : fileNames) {
			try {
				fileStorageService.deleteFile(fileName);
			} catch (Exception e) {
			}
		}
	}

	@Test
	public void testCreateFile() {
		try {
			String fileName = fileNames.get(0);
			fileStorageService.addFile(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			Byte[] content = fileStorageService.getFileContent(fileName, 1);
			assertNotNull(content);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testDuplicateCreateFile() {
		try {
			String fileName = fileNames.get(1);
			fileStorageService.addFile(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			fileStorageService.addFile(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			assertTrue(false);
		} catch (FileAlreadyExistsException fe) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testUpdateFile() {
		try {
			String fileName = fileNames.get(2);
			fileStorageService.addFile(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			fileStorageService.updateFile(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }), true);
			Byte[] content = fileStorageService.getFileContent(fileName, 2);
			assertNotNull(content);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testListFiles() {
		try {
			String fileName = fileNames.get(3);
			fileStorageService.addFile(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			List<FileDTO> files = fileStorageService.getAllFiles();
			assertNotNull(files);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testDeleteFile() {
		try {
			String fileName = fileNames.get(4);
			fileStorageService.addFile(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			fileStorageService.deleteFile(fileName);
			Byte[] content = fileStorageService.getFileContent(fileName, 1);
			assertNull(content);
		} catch (FileNotFoundException fe) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testInvalidDeleteFile() {
		try {
			String fileName = fileNames.get(5);
			fileStorageService.deleteFile(fileName);
			assertTrue(false);
		} catch (FileNotFoundException fe) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

}
