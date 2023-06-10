package com.ssongman.springredis;

import org.springframework.data.repository.CrudRepository;

public interface PersonRedisRepository extends CrudRepository<Person, String> {

}
