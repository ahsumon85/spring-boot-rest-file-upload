package com.ahasan.file.common.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "image.file")
public class FileStorageProperties {

	private String uploadDirLocate;

	public String getUploadDirLocate() {
		return uploadDirLocate;
	}

	public void setUploadDirLocate(String uploadDirLocate) {
		this.uploadDirLocate = uploadDirLocate;
	}

}
