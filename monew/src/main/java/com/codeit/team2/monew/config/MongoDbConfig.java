package com.codeit.team2.monew.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.codeit.team2.monew.module.domain.useractivity.repository")
public class MongoDbConfig {

}
