package com.lab.insurance.contract.creation.gateway.config;

import org.lab.insurance.contract.creation.integration.IntegrationConstants.Channels;
import org.lab.insurance.contract.creation.integration.IntegrationConstants.Queues;
import org.lab.insurance.model.contract.Contract;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.integration.dsl.amqp.AmqpOutboundEndpointSpec;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.integration.support.json.JsonObjectMapper;

@Configuration
public class IntegrationGatewayConfig {

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Bean
	public JsonObjectMapper<?, ?> mapper() {
		return new Jackson2JsonObjectMapper();
	}

	@Bean
	public IntegrationFlow flow() {

		AmqpOutboundEndpointSpec outbound = Amqp.outboundGateway(amqpTemplate).routingKey(Queues.ContractRequest);

		return IntegrationFlows.from(MessageChannels.publishSubscribe(Channels.ContractRequest)) //
				.transform(Transformers.toJson(mapper())) //
				.handle(outbound) //
				.transform(Transformers.fromJson(Contract.class, mapper())) //
				.channel(MessageChannels.direct(Channels.ContractResponse)) //
				.get();
	}
}