package com.leteatgo.domain.auth.repository;

import com.leteatgo.domain.auth.entity.RedisToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {

    Optional<RedisToken> findByAccessToken(String accessToken);

}
