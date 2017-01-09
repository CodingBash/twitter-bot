package com.codingbash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private Integer port;

	@Override
	protected String getDatabaseName() {
		return "memeUsers";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(host, port);
	}

	@Override
	protected String getMappingBasePackage() {
		return "com.codingbash";
	}
}