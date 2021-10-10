package com.self.rabbitmqlrn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitMqConfiguration {
	
	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.password}")
    private String password;
	@Value("${spring.rabbitmq.port}")
    private Integer port;
	@Value("${spring.rabbitmq.username}")
	private String userName;
	@Value("${camel.rabbitmq.vhost}")
	private String vHost;
	
	@Bean(name = "rabbitConnectionFactory")
	public ConnectionFactory buildRabbitConnectionFactory() {
		ConnectionFactory connFactory = new ConnectionFactory();
		connFactory.setHost(host);
		connFactory.setPort(port);
		connFactory.setUsername(userName);
		connFactory.setPassword(password);
		connFactory.setVirtualHost(vHost);
		return connFactory;
	}

}
