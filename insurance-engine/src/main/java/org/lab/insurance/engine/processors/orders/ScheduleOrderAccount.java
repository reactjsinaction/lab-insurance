package org.lab.insurance.engine.processors.orders;

import java.util.Date;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.Validate;
import org.lab.insurance.engine.ActionExecutionService;
import org.lab.insurance.engine.model.ActionEntity;
import org.lab.insurance.engine.model.orders.AccountOrderAction;
import org.lab.insurance.model.Constants;
import org.lab.insurance.model.insurance.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleOrderAccount implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduleOrderAccount.class);

	@Inject
	private ActionExecutionService actionExecutionService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Order order = exchange.getIn().getBody(Order.class);
		LOG.debug("Scheduling accounting for order {}", order);
		validateOrder(order);
		Date when = order.getDates().getValued();
		ActionEntity<?> action = new AccountOrderAction().withOrderId(order.getId()).withActionDate(when);
		actionExecutionService.schedule(action, when);
	}

	private void validateOrder(Order order) {
		Validate.notNull(order.getDates().getValued(), "order.validation.missingValuedDate");
		Validate.isTrue(order.getDates().getAccounted() == null, "order.validation.accountedDateMustBeNull");
		Validate.isTrue(order.getCurrentState().getStateDefinition().getId().equals(Constants.OrderStates.VALUED),
				"order.validation.expectedStateValued");
	}
}
