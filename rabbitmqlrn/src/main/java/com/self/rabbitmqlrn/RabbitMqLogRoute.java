package com.self.rabbitmqlrn;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("RabbitMqLogRoute")
public class RabbitMqLogRoute extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(RabbitMqLogRoute.class);
	@Override
	public void configure() throws Exception {
		CamelContext ctx = this.getContext();
		ctx.addComponent("rabbitmqLog", new RabbitMqLoggingComponent());
        from("file://{{in.directory}}/?delete=false")
        .split(method(new SplitWithNewLine(),"splitRecords"))
        .to("direct:split")
        .end();
        
        from("direct:split")
        .log("Got body:"+body())
        .setHeader(RabbitMQConstants.ROUTING_KEY, simple("${properties:camel.rabbitmq.routingKey}"))
        .to("rabbitmqLog:{{demo.exchangeName}}?connectionFactory=#rabbitConnectionFactory&declare=false&autoDelete=false&arg.queue.x-message-ttl=20000")
        .end();

	}

}
