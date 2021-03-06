package org.lab.insurance.model.contract;

import java.util.List;

import org.lab.insurance.model.insurance.OrderDistribution;

import lombok.Data;

@Data
public abstract class FinancialService {

	private List<OrderDistribution> sourceDistribution;
	private List<OrderDistribution> targetDistribution;

}
