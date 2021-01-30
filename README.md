# Spring Boot File Upload / Download With [spring-boot-rest-service](https://github.com/ahsumon85/spring-boot-rest-jpa-mysql)

## Overview

In this article, we focus on how to configure **multipart (file upload) support** in RESTful web service.

Spring allows us to enable this multipart support with pluggable `MultipartFile` interface. Spring provides a `MultipartFile` interface to handle HTTP multi-part requests for uploading files. Multipart-file requests break large files into smaller chunks which makes it efficient for file uploads.



