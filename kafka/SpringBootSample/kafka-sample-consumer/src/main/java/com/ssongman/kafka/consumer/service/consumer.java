package com.ssongman.kafka.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class consumer {

    @Value("${topic.name}")
    private String topicName;
	
	@KafkaListener(topics="${topic.name}")
	public void consuemrRecord(ConsumerRecord<String, String> payload) {	
		System.out.println("");
		System.out.println("Consume Message-----------------------");
		System.out.println("Topic: " + topicName);
		System.out.println("key: "     + payload.key());
		System.out.println("Headers: " + payload.headers());
		System.out.println("Partion: " + payload.partition());
		System.out.println("Value: "   + payload.value());		
	}

//    // Consume Only Message 
//	@KafkaListener(topics="${topic.name}", groupId="my-topic-group")
//	public void consuemrMessage(String message) {
//		System.out.println(message);
//	}
}
