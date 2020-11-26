package com.ahasan.file.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "employee")
public class EmployeeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_id")
	private Long employeeId;

	@Size(max = 10, min = 1, message = "employee name field must be equal or less than {max}")
	@Column(name = "emp_name")
	private String employeeName;

	@Size(max = 30, min = 1, message = "employee email field must be equal or less than {max}")
	@Column(name = "emp_email")
	private String employeeEmail;

	@Size(max = 6, min = 1, message = "employee gender field must be equal or less than {max}")
	@Column(name = "emp_gender")
	private String employeeGender;

	@Size(max = 14, min = 1, message = "employee phone field must be equal or less than {max}")
	@Column(name = "emp_phone")
	private String employeePhone;

	@Column(name = "file_name")
	private String fileName;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public String getEmployeeGender() {
		return employeeGender;
	}

	public void setEmployeeGender(String employeeGender) {
		this.employeeGender = employeeGender;
	}

	public String getEmployeePhone() {
		return employeePhone;
	}

	public void setEmployeePhone(String employeePhone) {
		this.employeePhone = employeePhone;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
