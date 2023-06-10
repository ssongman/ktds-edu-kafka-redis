package com.ssongman.kafka.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    @Value("${topic.name}")
    private String topicName;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	public void sendMeessage(String message) {
		System.out.println("[Producer.sendMeessage]-----------------------");
		System.out.println("Message: " + message);
		kafkaTemplate.send(topicName, message);
	}

}