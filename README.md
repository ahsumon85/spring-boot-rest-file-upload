# Spring Boot File Upload / Download With [spring-boot-rest-service](https://github.com/ahsumon85/spring-boot-rest-jpa-mysql)

## Overview

In this article, we focus on how to configure **multipart (file upload) support** in RESTful web service.

Spring allows us to enable this multipart support with pluggable `MultipartFile` interface. Spring provides a `MultipartFile` interface to handle HTTP multi-part requests for uploading files. Multipart-file requests break large files into smaller chunks which makes it efficient for file uploads.

### Tools you will need

* Maven 3.0+ is your build tool
* Your favorite IDE but we will recommend `STS-4-4.4.1 version`. We use STS.
* MySQL server
* JDK 1.8+

### How to run advance microservice?

**Application Running Process**:

- First we need to run `eureka service`
- Second we need to run `auth-service`
- Third we need to run `item-servic` and `sales-service`
- At last we need to run `gateway-service`, if we did run `gateway-service` before running `auth-service and iteam,sales-service` then we have to wait approximately 10 second 


 **Run on sts IDE**

 `click right button on the project >Run As >Spring Boot App`

After successfully run then we will refresh `eureka` dashboard and make sure to run `auth`, `item`, `sales` and `gateway` on the eureka dashboard.

Eureka Discovery-Service URL: `http://localhost:8761`

## Eureka Service

Eureka Server is an application that holds the information about all client-service applications. Every Micro service will register into the Eureka server and Eureka server knows all the client applications running on each port and IP address. Eureka Server is also known as Discovery Server.



## Authorization Service

An **Authorization Server** issues tokens to client applications on behalf of a **Resource** Owner for use in authenticating subsequent API calls to the **Resource Server**. The **Resource Server** hosts the protected **resources**, and can accept or respond to protected **resource** requests using access tokens.

### CORS filter Configure

Let’s create a class `WebSecurityConfiguration.java` to configure CORS Filte.

* **corsFilter** Apparently the Oauth2 endpoints and filters get processed before getting to the Spring Security filter chain, so adding CORS filters normally wouldn't work, but adding a CORS filter bean with high order priority ended up working.

  This is my dedicated configuration class for CORS (adapted from the official spring guide, I'll be tweaking it later)

```
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	  @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
```


### Test Authorization Service
**Get Access Token**

Let’s get the access token for `admin` by passing his credentials as part of header along with authorization details of appclient by sending `client_id` `client_pass` `username` `userpsssword`

Now hit the POST method URL via POSTMAN to get the OAUTH2 token.

**`http://localhost:8180/oauth/token`**

Now, add the Request Headers as follows −

* `Authorization` − Basic Auth with your `Client Id` and `Client secret`

* `Content Type` − application/x-www-form-urlencoded
![Screenshot from 2020-12-09 10-22-05](https://user-images.githubusercontent.com/31319842/101584943-ed47ff00-3a08-11eb-9d01-e196e0e089a6.png)

Now, add the Request Parameters as follows −

* `grant_type` = password
* `username` = your username
* ` password` = your password
![Screenshot from 2020-12-09 10-22-12](https://user-images.githubusercontent.com/31319842/101584942-ec16d200-3a08-11eb-9355-0e082a2493c7.png)

**HTTP POST Response**
```
{
    "access_token": "615ca239-7394-463a-8032-94dddd612dcf",
    "token_type": "bearer",
    "refresh_token": "33a3278e-4d62-4a93-8af6-11c0507b7a78",
    "expires_in": 3478,
    "scope": "READ WRITE"
}
```



## Item Service - resource service

Now we will see `micro-item-service` as a resource service. The `micro-item-service` a REST API that lets you CRUD (Create, Read, Update, and Delete) products. It creates a default set of items when the application loads using an `ItemApplicationRunner` bean.

### Setting Up Swagger 2 with a Item Service 

To enable the Swagger2 in Spring Boot application, you need to add the following dependencies in our build configurations file.

```
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger2</artifactId>
	<version>2.9.2</version>
</dependency>
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-bean-validators</artifactId>
	<version>2.9.2</version>
</dependency>
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger-ui</artifactId>
	<version>2.9.2</version>
</dependency>
```

Now, add the `@EnableSwagger2` annotation in your main Spring Boot application. The `@EnableSwagger2` annotation is used to enable the `Swagger2` for your Spring Boot application. Here have two variable that has `clientId` and `clientSecret` value getting from `application.properties`  file

The code for main Spring Boot application is shown below −

```
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	public static final String securitySchemaOAuth2 = "oauth2schema";
	public static final String authorizationScopeGlobal = "global";
	public static final String authorizationScopeGlobalDesc = "accessEverything";
}
```

### Swagger UI With an OAuth-Secured API

Next, create Docket Bean to configure Swagger2 for your Spring Boot application. We need to define the base package to configure REST API(s) for Swagger2.

The Swagger UI provides a number of very useful features that we've covered well so far here. But we can't really use most of these if our API is secured and not accessible.

Let's see how we can allow Swagger to access an OAuth-secured API using the Authorization Code grant type in this example.

We'll configure Swagger to access our secured API using the *SecurityScheme* and *SecurityContext* support:

```
	@Bean
	public Docket itemsApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
			.apis(RequestHandlerSelectors.basePackage("com.ahasan.sales.controller"))
					.paths(PathSelectors.any()).build()
					.securityContexts(Collections.singletonList(securityContext()))
					.securitySchemes(Arrays.asList(securitySchema())).apiInfo(apiInfo());
	}
	
	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}
```

After defining the *Docket* bean, its *select()* method returns an instance of *ApiSelectorBuilder*, which provides a way to control the endpoints exposed by Swagger.

We can configure predicates for selecting *RequestHandler*s with the help of *RequestHandlerSelectors* and *PathSelectors*. Using *any()* for both will make documentation for our entire API available through Swagger.

### Security Configuration

We'll define a *SecurityConfiguration* bean in our Swagger configuration and set some defaults:

```
@Bean
public SecurityConfiguration security() {
	return new SecurityConfiguration(clientId, clientSecret, "", "", "Bearer access 		token", ApiKeyVehicle.HEADER, HttpHeaders.AUTHORIZATION, "");
}
```

### SecurityScheme

Next, we'll define our *SecurityScheme*; this is used to describe how our API is secured (Basic Authentication, OAuth2, …).

```
private OAuth securitySchema() {
		List<AuthorizationScope> authorizationScopeList = newArrayList();
		authorizationScopeList.add(new AuthorizationScope("READ", "read all"));
		authorizationScopeList.add(new AuthorizationScope("WRITE", "access all"));
//		authorizationScopeList.add(new AuthorizationScope("TRUSTED", "trusted all"));
		List<GrantType> grantTypes = newArrayList();
		GrantType passwordCredentialsGrant = new 		  ResourceOwnerPasswordCredentialsGrant("http://localhost:9191/auth-api/oauth/token");
		grantTypes.add(passwordCredentialsGrant);
		return new OAuth("oauth2", authorizationScopeList, grantTypes);
	}
```

Note that we used the Authorization Code grant type, for which we need to provide a token endpoint and the authorization URL of our OAuth2 Authorization Server.

And here are the scopes we need to have defined:

```
private List<SecurityReference> defaultAuth() {
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[2];
		authorizationScopes[0] = new AuthorizationScope("READ", "read all");
		authorizationScopes[1] = new AuthorizationScope("WRITE", "write all");
//		authorizationScopes[2] = new AuthorizationScope("TRUSTED", "trust all");
		return Collections.singletonList(new SecurityReference("oauth2", authorizationScopes));
	}
```

### Web Security paths configure 

Ignoring security for path related to Swagger functionalities:

````
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/v2/api-docs",
						"/swagger-resources/**",
						"/swagger-ui.html",
						"/webjars/**",
						"/swagger/**");
	}
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
}
````

### Now we can test it in our browser by visiting

![Screenshot from 2020-12-07 15-41-54](https://user-images.githubusercontent.com/31319842/101335171-09319080-38a3-11eb-99d2-45972effff7b.png)

### Oauth2 login UI


![Screenshot from 2020-12-07 15-42-08](https://user-images.githubusercontent.com/31319842/101335180-0afb5400-38a3-11eb-9167-4b9a64a9c491.png)



### Test HTTP GET Request on item-service -resource service
```
curl --request GET 'localhost:8180/item-api/item/find' --header 'Authorization: Bearer 62e2545c-d865-4206-9e23-f64a34309787'
```
* Here `[http://localhost:8180/item-api/item/find]` on the `http` means protocol, `localhost` for hostaddress `8180` are gateway service port because every api will be transmit by the   
  gateway service, `item-api` are application context path of item service and `/item/find` is method URL.

* Here `[Authorization: Bearer 62e2545c-d865-4206-9e23-f64a34309787']` `Bearer` is toiken type and `62e2545c-d865-4206-9e23-f64a34309787` is auth service provided token

### For getting All API Information

On this repository we will see `secure-microservice-architecture.postman_collection.json` file, this file have to `import` on postman then we will ses all API information for testing api.




## Sales Service -resource service
Now we will see `micro-sales-service` as a resource service. The `micro-sales-service` a REST API that lets you CRUD (Create, Read, Update, and Delete) products. It creates a default set of items when the application loads using an `SalesApplicationRunner` bean.

### Setting Up Swagger 2 with a Item Service 

To enable the Swagger2 in Spring Boot application, you need to add the following dependencies in our build configurations file.

```
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger2</artifactId>
	<version>2.9.2</version>
</dependency>
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-bean-validators</artifactId>
	<version>2.9.2</version>
</dependency>
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger-ui</artifactId>
	<version>2.9.2</version>
</dependency>
```

Now, add the `@EnableSwagger2` annotation in your main Spring Boot application. The `@EnableSwagger2` annotation is used to enable the `Swagger2` for your Spring Boot application. Here have two variable that has `clientId` and `clientSecret` value getting from `application.properties`  file

The code for main Spring Boot application is shown below −

```
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	public static final String securitySchemaOAuth2 = "oauth2schema";
	public static final String authorizationScopeGlobal = "global";
	public static final String authorizationScopeGlobalDesc = "accessEverything";
}
```

### Swagger UI With an OAuth-Secured API

Next, create Docket Bean to configure Swagger2 for your Spring Boot application. We need to define the base package to configure REST API(s) for Swagger2.

The Swagger UI provides a number of very useful features that we've covered well so far here. But we can't really use most of these if our API is secured and not accessible.

Let's see how we can allow Swagger to access an OAuth-secured API using the Authorization Code grant type in this example.

We'll configure Swagger to access our secured API using the *SecurityScheme* and *SecurityContext* support:

```
	@Bean
	public Docket salesApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
			.apis(RequestHandlerSelectors.basePackage("com.ahasan.sales.controller"))
					.paths(PathSelectors.any()).build()
					.securityContexts(Collections.singletonList(securityContext()))
					.securitySchemes(Arrays.asList(securitySchema())).apiInfo(apiInfo());
	}
	
	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}
```

After defining the *Docket* bean, its *select()* method returns an instance of *ApiSelectorBuilder*, which provides a way to control the endpoints exposed by Swagger.

We can configure predicates for selecting *RequestHandler*s with the help of *RequestHandlerSelectors* and *PathSelectors*. Using *any()* for both will make documentation for our entire API available through Swagger.

### Security Configuration

We'll define a *SecurityConfiguration* bean in our Swagger configuration and set some defaults:

```
@Bean
public SecurityConfiguration security() {
	return new SecurityConfiguration(clientId, clientSecret, "", "", "Bearer access 		token", ApiKeyVehicle.HEADER, HttpHeaders.AUTHORIZATION, "");
}
```

### SecurityScheme

Next, we'll define our *SecurityScheme*; this is used to describe how our API is secured (Basic Authentication, OAuth2, …).

```
private OAuth securitySchema() {
		List<AuthorizationScope> authorizationScopeList = newArrayList();
		authorizationScopeList.add(new AuthorizationScope("READ", "read all"));
		authorizationScopeList.add(new AuthorizationScope("WRITE", "access all"));
//		authorizationScopeList.add(new AuthorizationScope("TRUSTED", "trusted all"));
		List<GrantType> grantTypes = newArrayList();
		GrantType passwordCredentialsGrant = new 		  ResourceOwnerPasswordCredentialsGrant("http://localhost:9191/auth-api/oauth/token");
		grantTypes.add(passwordCredentialsGrant);
		return new OAuth("oauth2", authorizationScopeList, grantTypes);
	}
```

Note that we used the Authorization Code grant type, for which we need to provide a token endpoint and the authorization URL of our OAuth2 Authorization Server.

And here are the scopes we need to have defined:

```
private List<SecurityReference> defaultAuth() {
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[2];
		authorizationScopes[0] = new AuthorizationScope("READ", "read all");
		authorizationScopes[1] = new AuthorizationScope("WRITE", "write all");
//		authorizationScopes[2] = new AuthorizationScope("TRUSTED", "trust all");
		return Collections.singletonList(new SecurityReference("oauth2", authorizationScopes));
	}
```

### Web Security paths configure 

Ignoring security for path related to Swagger functionalities:

````
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/v2/api-docs",
						"/swagger-resources/**",
						"/swagger-ui.html",
						"/webjars/**",
						"/swagger/**");
	}
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
}
````

### Now we can test it in our browser by visiting

`http://localhost:8180/sales-api/swagger-ui.html`

![Screenshot from 2020-12-07 15-22-21](https://user-images.githubusercontent.com/31319842/101333238-93c4c080-38a0-11eb-8f06-02c29558a31b.png)

### Oauth2 login UI
<img src="https://user-images.githubusercontent.com/31319842/101333236-932c2a00-38a0-11eb-8bdb-bde71fa98a7f.png" alt="Screenshot from 2020-12-07 15-22-53" style="height:%" />



### Test HTTP GET Request on resource service -resource service
```
curl --request GET 'localhost:8180/sales-api/sales/find' --header 'Authorization: Bearer 62e2545c-d865-4206-9e23-f64a34309787'
```
* Here `[http://localhost:8180/sales-api/sales/find]` on the `http` means protocol, `localhost` for hostaddress `8180` are gateway service port because every api will be transmit by the   
  gateway service, `sales-api` are application context path of item service and `/sales/find` is method URL.

* Here `[Authorization: Bearer 62e2545c-d865-4206-9e23-f64a34309787']` `Bearer` is toiken type and `62e2545c-d865-4206-9e23-f64a34309787` is auth service provided token


### For getting All API Information
On this repository we will see `secure-microservice-architecture.postman_collection.json` file, this file have to `import` on postman then we will ses all API information for testing api.



## API Gateway Service

Gateway Server is an application that transmit all API to desire services. every resource services information such us: `service-name, context-path` will beconfigured into the gateway service and every request will transmit configured services by gateway

### Hystrix configure on gateway service

Let's start by configuring hystrix monitoring dashboard on API Gateway Service application to view hystrix stream.

First, we need to add the `spring-cloud-starter-hystrix-dashboard` dependency:

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
	<version>1.4.7.RELEASE</version>
</dependency>
```

The main application class `ZuulApiGetWayRunner` to start Spring boot application.

```
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableHystrixDashboard
public class ZuulApiGetWayRunner {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApiGetWayRunner.class, args);
		System.out.println("Zuul server is running...");
	}

	@Bean
	public PreFilter preFilter() {
		return new PreFilter();
	}

	@Bean
	public PostFilter postFilter() {
		return new PostFilter();
	}

	@Bean
	public ErrorFilter errorFilter() {
		return new ErrorFilter();
	}

	@Bean
	public RouteFilter routeFilter() {
		return new RouteFilter();
	}
}
```

**@EnableHystrixDashBoard** – To give dashboard view of Hystrix stream.

**@EnableCircuitBreaker** – To enable Circuit breaker implementation.

**Zuul routes configuration** Open `application.properties` and add below entries-

```
#Set the Hystrix isolation policy to the thread pool
zuul.ribbon-isolation-strategy=thread

#each route uses a separate thread pool
zuul.thread-pool.use-separate-thread-pools=true
```

### Hystrix dashboard view

* To **monitor via Hystrix dashboard**, open Hystrix dashboard at `http://localhost:8180/hystrix`

![Screenshot from 2020-12-07 12-44-38](https://user-images.githubusercontent.com/31319842/101318202-1ee68c00-388a-11eb-8170-ca519491db1f.png)



* Now view **hystrix stream** in dashboard – `http://localhost:8180/hystrix.stream`

![Screenshot from 2020-12-07 12-04-26](https://user-images.githubusercontent.com/31319842/101317913-9c5dcc80-3889-11eb-8cc1-c757788dfbbd.png)



###  To make sure all service is runinng

After sucessfully run we can refresh Eureka Discovery-Service URL: `http://localhost:8761` will see `zuul-server` on eureka dashboard. the gateway instance will be run on `http://localhost:8180` port

![Screenshot from 2020-11-15 11-21-33](https://user-images.githubusercontent.com/31319842/99894579-6af0d880-2caf-11eb-84aa-d41b16cfbd12.png)

After we seen start auth, sales, item, zuul instance then we can try `advance-microservice-architecture.postman_collection.json` imported API from postman with token



# Docker-Deployment with [advance-microservice](https://github.com/ahsumon85/advance-spring-boot-microservice)

**Below we will see how to configure docker and docker-compose in microservice**

**To follow link**  [dockerized-spring-boot-microservice](https://github.com/ahsumon85/dockerized-spring-boot-microservice) 

