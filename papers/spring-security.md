## What is OAuth?

OAuth (Open Authorization) is an authorization protocol that allows third-party applications to access a user’s resources without sharing the user's credentials or identity.

It lets users grant limited access to their data on one site (e.g. Google) to another site (e.g. StackOverflow) without giving away their username and password.

First version introduced was OAuth 1.0 and the latest improved version is OAuth 2.0.

Concept in Simple Terms:

Everytime we try to login to a new site using an existing FB or google account, OAuth works under the hood to make this possible. What that means is - when we try to access a site like StackOverflow and are asked to login using an existing Google account, we click on a button and are redirected to Google account, where we are then asked to click on an allow button - this is the process of authorizing Google to share a limited set of information with StackOverflow.


Terminologies:

-> A few fundamental concepts in OAuth are discussed in this section - this would help us in mapping different actors involved in the authorization process.

1. Resource Owner : Owner of the Resource (Data of this person/resource is shared with third party). eg: Person with a Google Account.
2. Client: One who wants access on behalf of RO. e.g: StackOverflow
3. Authentication Server: Server that provides auth tokens. e.g: Google
4. Resource Server: API/Resource that client wants access to, is stored here. e.g: Again, Google.

Same actor can play multiple roles based on the situation.

-> Authorization Grant Types (Flows) are as listed below:

| Grant Type                                           | Use Case                                                 |
| ---------------------------------------------------- | -------------------------------------------------------- |
| Authorization Code                               | Web/mobile apps with user login                          |
| Implicit (deprecated)                            | Was used for browser-based apps (no longer recommended)  |
| Client Credentials                               | Machine-to-machine/server-to-server access               |
| Resource Owner Password Credentials (deprecated) | Username/password directly given to client (discouraged) |
| Device Code                                      | Devices without browsers (TVs, consoles)                 |

-> Two types of tokens are :
1. Access Token
2. Refresh Token

-> Different endpoints are:
1. /authorize – where the user grants access (for code flow)
2. /token – where tokens are issued
3. /revoke – (optional) to revoke tokens

## What is OpenID?

We often hear the terms - OAuth and OpenID together and there is a reason for that. Together they provide a complete solution to Authentication/Authorization challenges.

OpenID Connect is a layer on top of OAuth2 that adds authentication — so apps can know who we are. It’s like OAuth2 + ID card.

## Providers that support OIDC + OAuth2

1. Okta
2. Keycloak
3. Azure AD
4. Ping Identity
5. Github - GitHub supports OAuth2 only, but it has some custom APIs that can give you user identity. So people use it for login, but it’s not standards-compliant OpenID Connect.It's like a workaround.
6. Google
7. Auth0


## In-house OAuth2/OIDC server

If you decide to build your own OAuth2 server:

It must support OpenID Connect (OIDC) if you want identity (not just tokens).

It needs to expose:

.well-known/openid-configuration

Token endpoint

Authorization endpoint

Public keys endpoint (for verifying ID tokens)

Spring Boot will use that metadata to handle the rest.

## Sample Use Cases
To understand the concepts better, we will cover a few use cases and verify the implementation using SpringBoot and Java.

Use Case 1: Service is registered as a Client with google (Auth Server and Resource Server are same - Google)
Use Case 2: Enhance Use Case 1 to add a default route post verification
Use Case 3: client credentials - Service A accesses Service B securily using Auth0 / KeyCloak(Client and Resource Owner are Same)
Use Case 4: client credentials - Service A accesses Service B securily using Ping Identity
Use Case 5: Your own Auth Server - if possible.


**Use Case 1:**

Step 1:
To Add Google as Authentication Server and Resource Server, we would need to first create a google cloud project with OAuth2 credentials.

Follow the steps at https://console.cloud.google.com/apis/credentials to create free OAuth2 credentials.
Add sample data in all the fields, for redirect URL - add the following value - http://localhost:8080/login/oauth2/code/google. This is the default redirect URI template provided by Spring App. Note that the final path "google" in the above URL should match the registration id mentioned in application.yml file.(Refer Step 3)

Step 2:
Create a SpringBoot project from [spring](https://start.spring.io/). Add Spring Web, Spring Security and OAuth2 Client as dependency.

Alternatively, add the following dependencies to SpringBoot web project.
```
        <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```

Step 3:
Add following properties to application.yml file
```
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: <YOUR_CLIENT_ID>
            client-secret: <YOUR_CLIENT_SECRET>
            scope:
              - openid
              - profile
              - email
        provider:
          google:
            issuer-uri: https://accounts.google.com
```

Step 4: 
Add a default GET endpoint
```
    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser user) {
        return "Hello, " + user.getFullName() + "! Your email is " + user.getEmail();
    }
```

Step 5:
Hit the endpoint - http://localhost:8080

![Output](images/google-redirect.png)

In this use case, Google acts as both Authorization Server and Resource Server.



| Role                     | Definition                                                       | In Our Example                         |
| ------------------------ | ---------------------------------------------------------------- | ------------------------------------------- |
| Resource Owner      | The person who owns the data or identity.                        | The end user (You)logging in via Google.   |
| Client               | The app that wants to access the user’s data or log the user in. | Our Spring Boot application        |
| Authorization Server | Authenticates the user and gives tokens.                         | Google (accounts.google.com)          |
| Resource Server      | Hosts the user's protected data.                                 | Google (for full name, email) |


**Use Case 2:**

An interesting fact is - SpringBoot by default creates a login page at "/login".This is a page that lists available OAuth2 providers (like Google), with a “Login with Google” button.

If in our Controller class, we add a "/login" endpoint, it will be taken over by internal login page unless it's configured in security chain.


In this 2nd use case, we analyze a scenario, where the user would first access a base url and then access a "/home" endpoint. We provide access to base url without authentication but any specific endpoint should be accessed post authentication alone.

This is done by adding relevant endpoints in SecurityFilterChain Bean.

```
  @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(successHandler())
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        // always redirect to /home after login
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler("/home");
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }
```

## Code
For UseCase 1 and UseCase 2, check code -> https://github.com/r7b7/scalable-system-design/tree/spring-security-case-study/code/oauth/security