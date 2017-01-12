package com.codingbash;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.StringUtils;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

// TODO: Double check if production password is correct
// TODO: Look into Devtools remote debugging
@Configuration
@EnableMongoRepositories("com.codingbash.repository")
public class MongoConfiguration extends AbstractMongoConfiguration {

	@Value("${mongodb.name}")
	private String dbName;

	@Value("${mongodb.host}")
	private String host;

	@Value("${mongodb.port}")
	private Integer port;

	@Value("${mongodb.username}")
	private String userName;

	@Value("${mongodb.password}")
	private String password;

	@Override
	protected String getDatabaseName() {
		return this.dbName;
	}

	@Bean
	public MongoClient mongoClient() {
		final ServerAddress serverAddress = new ServerAddress(this.host, this.port);
		MongoClient mongoClient = null;
		if (!(StringUtils.isEmpty(this.userName) || StringUtils.isEmpty(this.password))) {
			final List<MongoCredential> mongoCredentials = Arrays.asList(MongoCredential
					.createScramSha1Credential(this.userName, getDatabaseName(), this.password.toCharArray()));
			mongoClient = new MongoClient(serverAddress, mongoCredentials);
			System.out.println("AUTHENTICATED: " + mongoCredentials.get(0).getUserName());
		} else {
			mongoClient = new MongoClient(serverAddress);
		}
		System.out.println(mongoClient.getCredentialsList().get(0).getPassword());
		return mongoClient;
	}

	@Override
	public Mongo mongo() throws Exception {
		return mongoClient();
	}

	@Bean
	public MongoDbFactory mongoDbFactory() {
		return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoDbFactory(), null);
	}

}
