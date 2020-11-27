package com.ahasan.file.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ahasan.file.common.messages.BaseResponse;
import com.ahasan.file.dto.EmployeeDTO;
import com.ahasan.file.service.EmployeeService;

@Validated
@RestController
@RequestMapping("/employee")
public class EmployeeController {


	@Autowired
	private EmployeeService employeeService;

	@GetMapping(value = "/find")
	public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
		List<EmployeeDTO> list = employeeService.findEmpList();
		return new ResponseEntity<List<EmployeeDTO>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/find/by-id")
	public ResponseEntity<EmployeeDTO> getEmployeeById(@RequestParam Long id) {
		EmployeeDTO list = employeeService.findByEmployeeId(id);
		return new ResponseEntity<EmployeeDTO>(list, HttpStatus.OK);
	}

	@PostMapping(value = { "/add", "/update" }, consumes = "multipart/form-data")
	public ResponseEntity<BaseResponse> createOrUpdateEmployee(@ModelAttribute EmployeeDTO employeeDTO) {
		BaseResponse response = employeeService.createOrUpdateEmployee(employeeDTO);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<BaseResponse> deleteEmployeeById(@PathVariable("id") Long id) {
		BaseResponse response= employeeService.deleteEmployeeById(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
