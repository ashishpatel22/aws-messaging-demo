package com.ericsson.aws.controllers.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:awssqs.properties")
public class SQSConfig {

}
