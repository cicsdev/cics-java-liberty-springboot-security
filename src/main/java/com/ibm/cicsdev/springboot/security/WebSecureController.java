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

import java.util.Date;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ibm.cics.server.InvalidRequestException;
import com.ibm.cics.server.Task;

@Controller
public class WebSecureController {

	
	/**
	 * @param model
	 * @param auth
	 * @return home
	 * @throws InvalidRequestException
	 */
	@GetMapping("/")
	public String home(Map<String, Object> model, Authentication auth) throws InvalidRequestException {
		
		// These values will be injected into home.html by the Thymeleaf templating engine
		model.put("message", "Hello World");
		model.put("title", "Hello Home");
		model.put("date", new Date());
		model.put("user", auth.getName());
		model.put("roles", ((UserDetails) auth.getPrincipal()).getAuthorities());
		
		// Check if we are running in CICS
		if (System.getProperty("com.ibm.cics.jvmserver.wlp.server.name") != null) {
			model.put("cicsuser", Task.getTask().getUSERID());
			model.put("tranid", Task.getTask().getTransactionName());
		}
		
		return "home";
	}
	
	
}
