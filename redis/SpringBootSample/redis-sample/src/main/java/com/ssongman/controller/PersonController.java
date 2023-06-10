package com.ssongman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ssongman.springredis.Person;
import com.ssongman.springredis.PersonRedisRepository;

@RestController
public class PersonController {

    @Autowired
    private PersonRedisRepository repo;

    @GetMapping("/health")
	public String getHealth() {
    	System.out.println("[getHealth]-----");
    	return "OK";
    }

    @PostMapping("/person")
	public Person setPerson(@RequestBody Person person) {
    	System.out.println("[setPerson]-----");
		return  repo.save(person);
	}

    @GetMapping("/person/{id}")
	public Person getPerson(@PathVariable("id") String id) {
    	System.out.println("[getPerson]-----");    	
    	return repo.findById(id).get();
    }

    @DeleteMapping("/person/{id}")
	public void deletePerson(@PathVariable("id") String id) {
    	System.out.println("[deletePerson]-----");
    	repo.deleteById(id);
    	return;
    }

}
