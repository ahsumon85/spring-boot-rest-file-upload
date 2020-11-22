package com.ahasan.file.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ahasan.file.common.messages.BaseResponse;
import com.ahasan.file.common.utils.FileStorageService;
import com.ahasan.file.dto.EmployeeDTO;
import com.ahasan.file.service.EmployeeService;

//@Validated
@RestController
@RequestMapping("/employee")
public class EmployeeController {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private EmployeeService employeeService;

	@GetMapping(value = "/find", produces = "application/json")
	public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
		List<EmployeeDTO> list = employeeService.findEmpList();
		return new ResponseEntity<List<EmployeeDTO>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/find/by-id", produces = "application/json")
	public ResponseEntity<EmployeeDTO> getEmployeeById(@RequestParam Long id) {
		EmployeeDTO list = employeeService.findByEmpId(id);
		return new ResponseEntity<EmployeeDTO>(list, HttpStatus.OK);
	}

	@PostMapping(value = { "/add", "/update" }, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<BaseResponse> createOrUpdateEmployee(@RequestParam("file") MultipartFile file,
																@RequestParam("employeeDTO") EmployeeDTO employeeDTO) {
//		String fileName = fileStorageService.storeFile(employeeDTO.getFile());
//		employeeDTO.setFileName(fileName);
		BaseResponse response = employeeService.createOrUpdateEmployee(employeeDTO);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") Long id) {
		employeeService.deleteEmployee(id);
		return new ResponseEntity<>("Data Delete sucessfully", HttpStatus.OK);
	}

	@PostMapping(value = "/uploadFile")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		String fileName = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/employee/downloadFile/")
				.path(fileName).toUriString();
		return new ResponseEntity<>(fileDownloadUri, HttpStatus.OK);
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
