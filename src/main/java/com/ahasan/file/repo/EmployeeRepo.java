package com.ahasan.file.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ahasan.file.entity.EmployeeEntity;

public interface EmployeeRepo extends JpaRepository<EmployeeEntity, Long> {

	public EmployeeEntity findByEmployeeId(Long empId);

}
