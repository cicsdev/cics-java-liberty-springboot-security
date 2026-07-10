/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* SAMPLE                                                                 */
/*                                                                        */
/* (c) Copyright IBM Corp. 2020 All Rights Reserved                       */
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */


package com.ibm.cicsdev.springboot.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Application implements WebMvcConfigurer
{
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}
	
	
	/**
	 * @param registry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry)
	{
		// Register our login page (found in the resources/templates folder) as a ViewController
		// The template 'login' HTML uses the Thymeleaf template engine for simplicity and convenience
		registry.addViewController("/login").setViewName("login");
	}

	
	/** This class allows you to override the default Web Security configuration */
	@EnableWebSecurity(debug = false)
	@Configuration
	protected static class ApplicationSecurity
	{
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
		{
			http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authz -> authz
				        // Allow access to URLs required for form login
				        .requestMatchers("/login", "/resources/**", "/j_security_check","css/**").permitAll()
						.anyRequest().authenticated()
				)
				// Use Jakarta EE pre-authentication and map these roles to Spring Security
				.jee(jee -> jee.mappableRoles("USER", "ADMIN"));
			
			return http.build();
		}
	}
}
