package com.codingbash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codingbash.model.MemeAccount;

public interface MemeAccountMongoRepository extends MongoRepository<MemeAccount, String> {

	public MemeAccount findByTwitterId(String twitterId);

}
