package org.metadata.filestorage.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FileStorageControllerTest {

	@Autowired
	private FileStorageController fileStorageController;

	private List<String> fileNames;

	@BeforeEach
	private void init() {
		fileNames = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			fileNames.add(String.format("test_file_%s.tmp", i));
		}
	}

	@AfterEach
	private void cleanup() {
		for (String fileName : fileNames) {
			try {
				fileStorageController.deleteFile(fileName);
			} catch (Exception e) {
			}
		}
	}

	@Test
	public void testCreateFile() {
		try {
			String fileName = fileNames.get(0);
			List<MultipartFile> files = new ArrayList<MultipartFile>();
			files.add(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			fileStorageController.addFile(files);
			ResponseEntity<?> resp = fileStorageController.downloadFile(fileName, 1);
			assertEquals(resp.getStatusCodeValue(), 200);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testListFile() {
		try {
			String fileName = fileNames.get(1);
			List<MultipartFile> files = new ArrayList<MultipartFile>();
			files.add(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			fileStorageController.addFile(files);
			ResponseEntity<?> resp = fileStorageController.getAllFiles();
			assertEquals(resp.getStatusCodeValue(), 200);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testDeleteFile() {
		try {
			String fileName = fileNames.get(2);
			List<MultipartFile> files = new ArrayList<MultipartFile>();
			files.add(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			fileStorageController.addFile(files);
			fileStorageController.deleteFile(fileName);
			ResponseEntity<?> resp = fileStorageController.downloadFile(fileName, 1);
			assertEquals(resp.getStatusCodeValue(), 204);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testUpdateFile() {
		try {
			String fileName = fileNames.get(3);
			List<MultipartFile> files = new ArrayList<MultipartFile>();
			files.add(new MockMultipartFile(fileName, fileName, null, new byte[] { 1, 1 }));
			fileStorageController.addFile(files);
			fileStorageController.updateFile(files, true);
			ResponseEntity<?> resp = fileStorageController.downloadFile(fileName, 2);
			assertEquals(resp.getStatusCodeValue(), 200);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

}
