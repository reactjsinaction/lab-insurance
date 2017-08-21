package org.lab.insurance.model.jpa.insurance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.lab.insurance.model.Constants;
import org.lab.insurance.model.HasContract;
import org.lab.insurance.model.HasState;
import org.lab.insurance.model.common.NotSerializable;
import org.lab.insurance.model.jpa.contract.Contract;
import org.lab.insurance.model.jpa.engine.State;
import org.lab.insurance.model.validation.ValidOrder;

/**
 * Representa un movimiento u operacion de entrada/salida de fondos en un contrato.
 */
@Entity
@Table(name = "INS_ORDER")
@SuppressWarnings("serial")
@NamedQueries({ @NamedQuery(name = "Order.selectByContractInStates", query = "select e from Order e where e.contract = :contract and e.currentState.stateDefinition.id in :stateIds order by e.dates.valueDate") })
@ValidOrder
public class Order implements Serializable, HasContract, HasState<String> {

	@Id
	@Column(name = "ID", length = 36)
	@GeneratedValue(generator = "system-uuid")
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", nullable = false, length = 32)
	private OrderType type;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH }, optional = false)
	@JoinColumn(name = "CONTRACT_ID", nullable = false)
	@NotSerializable
	private Contract contract;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "CURRENT_STATE_ID")
	private State currentState;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "ORDER_PROCESS_INFO")
	private OrderProcessInfo processInfo;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinTable(name = "INS_ORDER_DISTRIBUTION_SELL", joinColumns = { @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID") }, inverseJoinColumns = @JoinColumn(name = "DISTRIBUTION_ID", referencedColumnName = "ID"))
	private List<OrderDistribution> sellDistribution;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinTable(name = "INS_ORDER_DISTRIBUTION_BUY", joinColumns = { @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID") }, inverseJoinColumns = @JoinColumn(name = "DISTRIBUTION_ID", referencedColumnName = "ID"))
	private List<OrderDistribution> buyDistribution;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<MarketOrder> marketOrders;

	@Embedded
	private OrderDates dates;

	@Column(name = "GROSS_AMOUNT", nullable = false, precision = 20, scale = 7)
	private BigDecimal grossAmount;

	@Column(name = "NET_AMOUNT", precision = 20, scale = 7)
	private BigDecimal netAmount;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	@Override
	public Contract getContract() {
		return contract;
	}

	@Override
	public void setContract(Contract policy) {
		this.contract = policy;
	}

	public OrderDates getDates() {
		return dates;
	}

	public void setDates(OrderDates dates) {
		this.dates = dates;
	}

	public List<OrderDistribution> getBuyDistribution() {
		return buyDistribution;
	}

	public void setBuyDistribution(List<OrderDistribution> buyDistribution) {
		this.buyDistribution = buyDistribution;
	}

	public List<OrderDistribution> getSellDistribution() {
		return sellDistribution;
	}

	public void setSellDistribution(List<OrderDistribution> sellDistribution) {
		this.sellDistribution = sellDistribution;
	}

	public List<MarketOrder> getMarketOrders() {
		return marketOrders;
	}

	public void setMarketOrders(List<MarketOrder> marketOrders) {
		this.marketOrders = marketOrders;
	}

	@Override
	public State getCurrentState() {
		return currentState;
	}

	@Override
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public BigDecimal getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(BigDecimal grossAmount) {
		this.grossAmount = grossAmount;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public OrderProcessInfo getProcessInfo() {
		return processInfo;
	}

	public void setProcessInfo(OrderProcessInfo processInfo) {
		this.processInfo = processInfo;
	}

	public boolean isValued() {
		String currentStateId = (currentState != null) ? currentState.getStateDefinition().getId() : null;
		boolean checkDate = dates != null && dates.getValued() != null;
		boolean checkState = currentStateId != null && (currentStateId.equals(Constants.OrderStates.VALUED) || currentStateId.equals(Constants.OrderStates.ACCOUNTED));
		return checkDate && checkState;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(type != null ? "Order" : type);
		sb.append("/");
		sb.append(contract != null ? contract.getNumber() : "<null>");
		sb.append("/");
		sb.append(dates != null && dates.getEffective() != null ? DateFormatUtils.ISO_DATE_FORMAT.format(dates.getEffective()) : "<null>");
		sb.append("/");
		sb.append(dates != null && dates.getValueDate() != null ? DateFormatUtils.ISO_DATE_FORMAT.format(dates.getValueDate()) : "<null>");
		return sb.toString();
	}
}