package org.lab.insurance.engine.processors.policy;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.lab.insurance.model.Constants;
import org.lab.insurance.model.HasPolicy;
import org.lab.insurance.model.jpa.Policy;
import org.lab.insurance.model.jpa.insurance.Order;
import org.lab.insurance.model.jpa.insurance.OrderType;
import org.lab.insurance.model.matchers.OrderTypeMatcher;
import org.lab.insurance.services.common.StateMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.lambdaj.Lambda;

/**
 * Procesador encargado de persistir la informacion de una poliza y procesar el pago inicial.
 */
public class NewPolicyProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(NewPolicyProcessor.class);

	@Inject
	private Provider<EntityManager> entityManagerProvider;
	@Inject
	private Validator validator;
	@Inject
	private CamelContext camelContext;
	@Inject
	private StateMachineService stateMachineService;

	@Override
	public void process(Exchange exchange) throws Exception {
		LOG.info("Processing new policy");
		Policy policy = exchange.getIn().getBody(HasPolicy.class).getPolicy();
		Set<ConstraintViolation<Policy>> validationResults = validator.validate(policy);
		if (!validationResults.isEmpty()) {
			throw new RuntimeException("Validation errors");
		}
		EntityManager entityManager = entityManagerProvider.get();
		// Actualizamos la referencia de las ordenes
		for (Order order : policy.getOrders()) {
			order.setPolicy(policy);
		}
		entityManager.persist(policy);
		entityManager.flush();
		stateMachineService.createTransition(policy, Constants.PolicyStates.INITIAL);
		// Enviamos las ordenes a la cola de procesamiento (no veo la forma de hacerlo a traves del route)
		ProducerTemplate producer = camelContext.createProducerTemplate();
		for (Order order : Lambda.select(policy.getOrders(), new OrderTypeMatcher(OrderType.INITIAL_PAYMENT))) {
			order.setPolicy(policy);
			producer.requestBody("direct:order_process", order);
		}
	}
}
