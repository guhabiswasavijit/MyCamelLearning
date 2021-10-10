package com.self.rabbitmqlrn;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.component.rabbitmq.RabbitMQComponent;
import org.apache.camel.component.rabbitmq.RabbitMQEndpoint;
import org.apache.camel.util.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.ConnectionFactory;

@Component(value="rabbitmqLog")
public class RabbitMqLoggingComponent extends RabbitMQComponent {
	
	private static final Logger LOG = LoggerFactory.getLogger(RabbitMqLoggingComponent.class);
	 @Override
	 protected RabbitMQEndpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {

	        String host = getHostname();
	        int port = getPortNumber();
	        String exchangeName = remaining;
	        
	        LOG.debug("URI entered:{}", uri);
	        LOG.debug("URI remaining entered:{}", remaining);
	        LOG.debug("URI params entered:");
	        params.forEach((key,value) -> {
	        	LOG.debug("Param key:{}",key);
	        	LOG.debug("Param value:{}",value);
	        });

	        if (remaining.contains(":") || remaining.contains("/")) {
	            LOG.warn(
	                    "The old syntax rabbitmq://hostname:port/exchangeName is deprecated. You should configure the hostname on the component or ConnectionFactory");
	            try {
	                URI u = new URI("http://" + remaining);
	                host = u.getHost();
	                port = u.getPort();
	                if (u.getPath().trim().length() > 1) {
	                    exchangeName = u.getPath().substring(1);
	                } else {
	                    exchangeName = "";
	                }
	            } catch (Exception e) {
	                // ignore
	            }
	        }

	        // ConnectionFactory reference
	        ConnectionFactory connectionFactory = resolveAndRemoveReferenceParameter(params, "connectionFactory",
	                ConnectionFactory.class, getConnectionFactory());

	        // try to lookup if there is a single instance in the registry of the
	        // ConnectionFactory
	        if (connectionFactory == null && isAutoDetectConnectionFactory()) {
	            Map<String, ConnectionFactory> map = getCamelContext().getRegistry().findByTypeWithName(ConnectionFactory.class);
	            if (map != null && map.size() == 1) {
	                Map.Entry<String, ConnectionFactory> entry = map.entrySet().iterator().next();
	                connectionFactory = entry.getValue();
	                String name = entry.getKey();
	                if (name == null) {
	                    name = "anonymous";
	                }
	                LOG.info(
	                        "Auto-detected single instance: {} of type ConnectionFactory in Registry to be used as ConnectionFactory when creating endpoint: {}",
	                        name, uri);
	            }
	        }
	        String vhost = null;
	        String userName = null;
	        String password = null;
	        RabbitMQEndpoint endpoint;
	        if (connectionFactory == null) {
	            endpoint = new RabbitMQEndpoint(uri, this);
	        } else {
	            LOG.debug("Got Connection Factory:");
	            host = connectionFactory.getHost();
	            port = connectionFactory.getPort();
	            vhost = connectionFactory.getVirtualHost();
	            userName = connectionFactory.getUsername();
	            password = connectionFactory.getPassword();
	        	endpoint = new RabbitMQEndpoint(uri, this, connectionFactory);
	        }
	        endpoint.setHostname(host);
	        endpoint.setPortNumber(port);
	        endpoint.setUsername(userName);
	        endpoint.setPassword(password);
	        endpoint.setVhost(vhost);
	        endpoint.setAddresses(getAddresses());
	        endpoint.setThreadPoolSize(getThreadPoolSize());
	        endpoint.setExchangeName(exchangeName);
	        endpoint.setSslProtocol(getSslProtocol());
	        endpoint.setConnectionTimeout(getConnectionTimeout());
	        endpoint.setRequestedChannelMax(getRequestedChannelMax());
	        endpoint.setRequestedFrameMax(getRequestedFrameMax());
	        endpoint.setRequestedHeartbeat(getRequestedHeartbeat());
	        endpoint.setAutomaticRecoveryEnabled(getAutomaticRecoveryEnabled());
	        endpoint.setNetworkRecoveryInterval(getNetworkRecoveryInterval());
	        endpoint.setTopologyRecoveryEnabled(getTopologyRecoveryEnabled());
	        endpoint.setPrefetchEnabled(isPrefetchEnabled());
	        endpoint.setPrefetchSize(getPrefetchSize());
	        endpoint.setPrefetchCount(getPrefetchCount());
	        endpoint.setPrefetchGlobal(isPrefetchGlobal());
	        endpoint.setChannelPoolMaxSize(getChannelPoolMaxSize());
	        endpoint.setChannelPoolMaxWait(getChannelPoolMaxWait());
	        endpoint.setRequestTimeout(getRequestTimeout());
	        endpoint.setRequestTimeoutCheckerInterval(getRequestTimeoutCheckerInterval());
	        endpoint.setTransferException(isTransferException());
	        endpoint.setPublisherAcknowledgements(isPublisherAcknowledgements());
	        endpoint.setPublisherAcknowledgementsTimeout(getPublisherAcknowledgementsTimeout());
	        endpoint.setGuaranteedDeliveries(isGuaranteedDeliveries());
	        endpoint.setMandatory(isMandatory());
	        endpoint.setImmediate(isImmediate());
	        endpoint.setAutoAck(isAutoAck());	        
	        endpoint.setAutoDelete(Boolean.valueOf((String)params.get("autoDelete")));
	        LOG.debug("isAutoDelete():{}",endpoint.isAutoDelete());
	        LOG.debug("isDurable():{}",isDurable());
	        endpoint.setDurable(isDurable());
	        LOG.debug("isExclusive():{}",isExclusive());
	        endpoint.setExclusive(isExclusive());
	        endpoint.setExclusiveConsumer(isExclusiveConsumer());
	        endpoint.setPassive(isPassive());
	        LOG.debug("isSkipExchangeDeclare():{}",isSkipExchangeDeclare());
	        endpoint.setSkipExchangeDeclare(isSkipExchangeDeclare());
	        LOG.debug("isSkipQueueBind():{}",isSkipQueueBind());
	        endpoint.setSkipQueueBind(isSkipQueueBind());
	        LOG.debug("isSkipQueueDeclare():{}",isSkipQueueDeclare());
	        endpoint.setSkipQueueDeclare(isSkipQueueDeclare());	        
	        endpoint.setDeclare(Boolean.valueOf((String)params.get("declare")));
	        LOG.debug("isDeclare():{}", endpoint.isDeclare());
	        endpoint.setDeadLetterExchange(getDeadLetterExchange());
	        endpoint.setDeadLetterExchangeType(getDeadLetterExchangeType());
	        endpoint.setDeadLetterQueue(getDeadLetterQueue());
	        endpoint.setDeadLetterRoutingKey(getDeadLetterRoutingKey());
	        endpoint.setAllowNullHeaders(isAllowNullHeaders());

	        if (LOG.isDebugEnabled()) {
	            LOG.debug("Creating RabbitMQEndpoint with host {}:{} and exchangeName: {}",
	                    endpoint.getHostname(), endpoint.getPortNumber(), endpoint.getExchangeName());
	        }

	        Map<String, Object> localArgs = new HashMap<>();
	        if (getArgs() != null) {
	            // copy over the component configured args
	            localArgs.putAll(getArgs());
	        }
	        localArgs.putAll(PropertiesHelper.extractProperties(params, ARG_PREFIX));
	        Map<String, Object> existing = endpoint.getArgs();
	        if (existing != null) {
	            existing.putAll(localArgs);
	        } else {
	            endpoint.setArgs(localArgs);
	        }

	        LOG.debug("Other proeprties:{}",params);
	        setProperties(endpoint, params);

	        return endpoint;
	    }

}
