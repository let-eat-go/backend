package com.leteatgo.domain.auth.repository;

import com.leteatgo.domain.auth.entity.RedisSms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisSmsRepository extends CrudRepository<RedisSms, String> {

}
