package org.metadata.filestorage.controller;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.metadata.filestorage.common.FileStorageConstants;
import org.metadata.filestorage.pojo.FileDTO;
import org.metadata.filestorage.pojo.ProcessedFile;
import org.metadata.filestorage.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/filestorage/file")
@Api(value = "File Manamgement REST APIs", tags = "File Storage Management")
public class FileStorageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageController.class);

	private FileStorageService fileStorageService;

	@Autowired
	public void init(@Qualifier(FileStorageConstants.DB_FILE_STORAGE_SERVICE) FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = "Uploads files to storage")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Successfully uploaded"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<?> addFile(@RequestPart("files") List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		int storedFileCount = 0;
		List<ProcessedFile> processedFiles = new ArrayList<ProcessedFile>(files.size());
		for (MultipartFile file : files) {
			try {
				fileStorageService.addFile(file);
				processedFiles.add(new ProcessedFile(file.getOriginalFilename(), true, "success"));
				storedFileCount++;
			} catch (Exception ex) {
				LOGGER.error(String.format("Failed to add file '%s'", file.getOriginalFilename()), ex);
				processedFiles.add(new ProcessedFile(file.getOriginalFilename(), false, ex.getMessage()));
			}
		}
		if (storedFileCount == 0) {
			return ResponseEntity.internalServerError().body(processedFiles);
		}
		return ResponseEntity.ok().body(processedFiles);
	}

	@PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = "Updates files in storage")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Successfully updated"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<?> updateFile(@RequestPart("files") List<MultipartFile> files,
			@Parameter(description = "Indicates whether existing file should be overwritten") @RequestParam(name = "overwrite", required = false) Boolean overwrite) {
		int storedFileCount = 0;
		List<ProcessedFile> processedFiles = new ArrayList<ProcessedFile>(files.size());
		for (MultipartFile file : files) {
			try {
				fileStorageService.updateFile(file, overwrite);
				processedFiles.add(new ProcessedFile(file.getOriginalFilename(), true, "success"));
				storedFileCount++;
			} catch (Exception ex) {
				LOGGER.error(String.format("Failed to update file '%s'", file.getOriginalFilename()), ex);
				processedFiles.add(new ProcessedFile(file.getOriginalFilename(), false, ex.getMessage()));
			}
		}
		if (storedFileCount == 0) {
			return ResponseEntity.internalServerError().body(processedFiles);
		}
		return ResponseEntity.ok().body(processedFiles);
	}

	@GetMapping
	@ApiOperation(value = "List all files")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Fetched all files"),
			@ApiResponse(responseCode = "204", description = "No content"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<?> getAllFiles() {
		try {
			List<FileDTO> files = fileStorageService.getAllFiles();
			return ResponseEntity.ok().body(files);
		} catch (FileNotFoundException fe) {
			return ResponseEntity.noContent().build();
		} catch (Exception ex) {
			LOGGER.error("Failed to load files", ex);
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/download")
	@ApiOperation(value = "Downloads file")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Fetched file"),
			@ApiResponse(responseCode = "204", description = "No content"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<?> downloadFile(
			@Parameter(description = "File name") @RequestParam(name = "name", required = true) String fileName,
			@Parameter(description = "File version") @RequestParam(name = "version", required = false) Integer fileVersion) {
		try {
			Byte[] fileContent = fileStorageService.getFileContent(fileName, fileVersion);
			if (fileContent == null || fileContent.length <= 0) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
					.body(ArrayUtils.toPrimitive(fileContent));
		} catch (IllegalArgumentException le) {
			return ResponseEntity.badRequest().build();
		} catch (FileNotFoundException fe) {
			return ResponseEntity.noContent().build();
		} catch (Exception ex) {
			LOGGER.error("Failed to download file", ex);
			return ResponseEntity.internalServerError().build();
		}
	}

	@DeleteMapping
	@ApiOperation(value = "Deletes file")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "File deleted"),
			@ApiResponse(responseCode = "204", description = "No content"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<?> deleteFile(
			@Parameter(description = "File name") @RequestParam(name = "name", required = true) String fileName) {
		try {
			fileStorageService.deleteFile(fileName);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException le) {
			return ResponseEntity.badRequest().build();
		} catch (FileNotFoundException fe) {
			return ResponseEntity.noContent().build();
		} catch (Exception ex) {
			LOGGER.error("Failed to delete file", ex);
			return ResponseEntity.internalServerError().build();
		}
	}

}
