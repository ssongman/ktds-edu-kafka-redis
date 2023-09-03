package com.ssongman.kafka.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssongman.kafka.producer.service.Producer;

@RestController
public class PubController {
	
	private final Producer producer;
	
	@Autowired
	public PubController(Producer producer) {
		this.producer = producer;
	}
	
	@GetMapping("/publish")
	public void messageToTopic(@RequestParam("message") String message) {
		producer.sendMeessage(message);
	}

}
