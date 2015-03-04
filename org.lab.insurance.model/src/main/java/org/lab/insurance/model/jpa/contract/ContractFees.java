package org.lab.insurance.model.jpa.contract;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("CF")
@Table(name = "C_LOCK_IN")
@SuppressWarnings("serial")
public class ContractFees extends FinancialService {

}
