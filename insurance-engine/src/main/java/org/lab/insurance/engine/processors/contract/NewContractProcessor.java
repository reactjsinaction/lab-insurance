package org.lab.insurance.engine.processors.contract;

import javax.inject.Inject;
import javax.validation.Validator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.lab.insurance.services.common.StateMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Procesador encargado de persistir la informacion de una poliza y procesar el pago inicial.
 */
public class NewContractProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(NewContractProcessor.class);

	@Inject
	private Validator validator;
	@Inject
	private StateMachineService stateMachineService;

	@Override
	public void process(Exchange exchange) throws Exception {
		throw new RuntimeException("Not implemented");
		// LOG.info("Processing new policy");
		// Contract contract = exchange.getIn().getBody(HasContract.class).getContract();
		// Set<ConstraintViolation<Contract>> validationResults = validator.validate(contract);
		// if (!validationResults.isEmpty()) {
		// throw new RuntimeException("Validation errors");
		// }
		// EntityManager entityManager = entityManagerProvider.get();
		// // Actualizamos la referencia de las ordenes
		// for (Order order : contract.getOrders()) {
		// order.setContract(contract);
		// }
		// entityManager.persist(contract);
		// entityManager.flush();
		// stateMachineService.createTransition(contract, Constants.ContractStates.INITIAL);
	}
}
