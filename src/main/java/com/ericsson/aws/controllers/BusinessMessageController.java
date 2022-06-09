/**
 * 
 */
package com.ericsson.aws.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.aws.models.BusinessMessage;
import com.ericsson.aws.services.BusinessMessageProcessingService;

/**
 * @author ekumaaa
 *
 */
@RestController
@RequestMapping("/api/message")
public class BusinessMessageController {

	@Autowired
	BusinessMessageProcessingService businessMessageProcessingService;

	@PostMapping
	public String sendMessage(@Validated @RequestBody BusinessMessage businessMessage) {
		businessMessageProcessingService.processBusinessMessage(businessMessage);
		return "{\"result\":\"SUCCSS\"}";
	}

	@GetMapping
	public List<BusinessMessage> findAllMessages() {
		return businessMessageProcessingService.fetchAllTheMessages();
	}
}
