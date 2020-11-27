package com.ahasan.file.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ahasan.file.common.utils.FileStorageService;
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
		EmployeeEntity employeeEntity = employeeRepo.findByEmployeeId(employeeId);
		return copyEmployeeEntityToDto(employeeEntity);
	}

	public void createOrUpdateEmployee(EmployeeDTO employeeDTO) {
		employeeDTO.setFileName(provideFileDownloadUrlFrmMultipart(employeeDTO.getFile()));
		employeeRepo.save(copyEmployeeDtoToEntity(employeeDTO));
	}

	public void deleteEmployeeById(Long employeeId) {
		employeeRepo.deleteById(employeeId);
	}

	public String provideFileDownloadUrlFrmMultipart(MultipartFile file) {
		String fileName = fileStorageService.storeFile(file);
		return ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName).toUriString();
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
