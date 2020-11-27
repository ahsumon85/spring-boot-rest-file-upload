package com.ahasan.file.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ahasan.file.common.exceptions.CustomDataIntegrityViolationException;
import com.ahasan.file.common.exceptions.RecordNotFoundException;
import com.ahasan.file.common.messages.BaseResponse;
import com.ahasan.file.common.messages.CustomMessage;
import com.ahasan.file.common.utils.FileStorageService;
import com.ahasan.file.common.utils.Topic;
import com.ahasan.file.dto.EmployeeDTO;
import com.ahasan.file.entity.EmployeeEntity;
import com.ahasan.file.repo.EmployeeRepo;

@Service
@Transactional
public class EmployeeService {

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private EmployeeRepo employeeRepo;

	public List<EmployeeDTO> findEmpList() {
		return employeeRepo.findAll().stream().map(this::copyEmployeeEntityToDto).collect(Collectors.toList());
	}

	public EmployeeDTO findByEmployeeId(Long employeeId) {
		if (employeeRepo.existsById(employeeId)) {
			EmployeeEntity employeeEntity = employeeRepo.findByEmployeeId(employeeId);
			return copyEmployeeEntityToDto(employeeEntity);
		} else {
			throw new RecordNotFoundException(CustomMessage.DOESNOT_EXIT + employeeId);
		}

	}

	public BaseResponse createOrUpdateEmployee(EmployeeDTO employeeDTO) {
		try {
			employeeDTO.setFileName(provideFileDownloadUrlFrmMultipart(employeeDTO.getFile()));
			employeeRepo.save(copyEmployeeDtoToEntity(employeeDTO));
		} catch (DataIntegrityViolationException ex) {
			throw new CustomDataIntegrityViolationException(ex.getCause().getCause().getMessage());
		}
		return new BaseResponse(Topic.EMPLOYEE.getName() + CustomMessage.SAVE_SUCCESS_MESSAGE);
	}

	public BaseResponse deleteEmployeeById(Long employeeId) {
		if (employeeRepo.existsById(employeeId)) {
			employeeRepo.deleteById(employeeId);
		} else {
			throw new RecordNotFoundException(CustomMessage.NO_RECOURD_FOUND + employeeId);
		}
		return new BaseResponse(Topic.EMPLOYEE.getName() + CustomMessage.DELETE_SUCCESS_MESSAGE);
	}

	public String provideFileDownloadUrlFrmMultipart(MultipartFile file) {
		String fileName = fileStorageService.storeFile(file);
		return ServletUriComponentsBuilder.fromCurrentContextPath().path("/employee/downloadFile/").path(fileName)
				.toUriString();
	}

	private EmployeeDTO copyEmployeeEntityToDto(EmployeeEntity employeeEntity) {
		EmployeeDTO employeeDTO = new EmployeeDTO();
		BeanUtils.copyProperties(employeeEntity, employeeDTO);
		return employeeDTO;
	}

	private EmployeeEntity copyEmployeeDtoToEntity(EmployeeDTO employeeDTO) {
		EmployeeEntity employeeEntity = new EmployeeEntity();
		BeanUtils.copyProperties(employeeDTO, employeeEntity);
		return employeeEntity;
	}

}
