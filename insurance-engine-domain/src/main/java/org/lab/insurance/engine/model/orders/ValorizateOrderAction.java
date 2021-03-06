package org.lab.insurance.engine.model.orders;

import org.lab.insurance.engine.model.ActionDefinition;

@ActionDefinition(endpoint = "direct:order_valorizarion")
@SuppressWarnings("serial")
public class ValorizateOrderAction extends OrderBaseAction {

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ValorizateOrder/");
		sb.append(order != null ? order.getId() : "<null>");
		sb.append("/");
		// sb.append(actionDate != null ? DateFormatUtils.ISO_DATE_FORMAT.format(actionDate) : "<null>");
		return sb.toString();
	}
}
