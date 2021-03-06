package org.lab.insurance.bdd.contract.creation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.lab.insurance.bdd.contract.MongoTestOperations;
import org.lab.insurance.contract.creation.integration.domain.ContractCreationData;
import org.lab.insurance.model.contract.Contract;
import org.lab.insurance.model.contract.ContractPersonRelation;
import org.lab.insurance.model.contract.RelationType;
import org.lab.insurance.model.contract.repository.ContractRepository;
import org.lab.insurance.model.insurance.BaseAsset;
import org.lab.insurance.model.insurance.Order;
import org.lab.insurance.model.insurance.OrderDistribution;
import org.lab.insurance.model.insurance.OrderType;
import org.lab.insurance.model.legalentity.Person;
import org.lab.insurance.model.legalentity.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lab.insurance.contract.creation.gateway.messaging.ContractCreationGateway;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContractCreationSteps extends ContractCreationIntegrationTest {

	@Autowired
	protected ContractCreationGateway contractCreationGateway;

	@Autowired
	protected ContractRepository contractRepository;
	@Autowired
	protected PersonRepository personRepository;
	@Autowired
	protected MongoTestOperations mongoTestOperations;

	protected ContractCreationData contractCreateInfo;
	protected Contract contract;
	protected String contractNumber;

	@When("^Inicializo la base de datos$")
	public void inicializo_la_base_de_datos() {
		mongoTestOperations.resetDataBase();
	}

	@When("^Preparo contrato con acuerdo (\\w+)$")
	public void preparo_contrato(String agreementCode) {
		contractCreateInfo = new ContractCreationData();
		contractCreateInfo.setAgreementCode(agreementCode);
	}

	@When("^Establezco como suscriptor del contrato a la persona identificada con (\\w+)$")
	public void establezco_como_suscriptor_del_contrato_a_la_persona_identificada_con_(String idCardNumber) {
		Person person = personRepository.findByIdCardNumber(idCardNumber);
		Assert.assertNotNull(person);
		addRelation(person, RelationType.SUSCRIPTOR);
	}

	@When("^Establezco como beneficiario del contrato a la persona identificada con (\\w+)$")
	public void establezco_como_beneficiario_del_contrato_a_la_persona_identificada_con_W(String idCardNumber) {
		Assert.assertNotNull(contractCreateInfo);
		Person person = personRepository.findByIdCardNumber(idCardNumber);
		Assert.assertNotNull(person);
		addRelation(person, RelationType.RECIPIENT);
	}

	private void addRelation(Person person, RelationType type) {
		ContractPersonRelation relation = new ContractPersonRelation();
		relation.setContract(contract);
		relation.setPerson(person);
		relation.setType(type);
		if (contractCreateInfo.getRelations() == null) {
			contractCreateInfo.setRelations(new ArrayList<>());
		}
		contractCreateInfo.getRelations().add(relation);

	}

	@When("^Establezco un pago inicial neto de ([\\d|\\.]+) euros$")
	public void establezco_un_pago_inicial_neto_de_euros(BigDecimal amount) {
		Order initialPayment = Order.builder().type(OrderType.INITIAL_PAYMENT).build();
		initialPayment.setNetAmount(amount);
		contractCreateInfo.setInitialPayment(initialPayment);
	}

	@When("^Establezco la distribucion del pago inicial en \\((.+)\\)$")
	public void establezco_la_distribucion_del_pago_inicial_en_ASSET_ASSET(String distribution) {
		Order initialPayment = contractCreateInfo.getInitialPayment();
		initialPayment.setBuyDistribution(new ArrayList<>());
		log.debug("Parsing distribution {}", distribution);
		StringTokenizer stringTokenizer = new StringTokenizer(distribution, ";");
		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			String[] split = token.split(":");
			String isin = StringUtils.trimAllWhitespace(split[0]);
			OrderDistribution i = new OrderDistribution();
			i.setAsset(BaseAsset.builder().isin(isin).build());
			i.setPercent(new BigDecimal(StringUtils.trimWhitespace(split[1].replaceAll("%", ""))));
			initialPayment.getBuyDistribution().add(i);
		}
	}

	@When("^Establezco la fecha de contratacion a (\\d+)/(\\d+)/(\\d+)$")
	public void establezco_la_fecha_de_contratacion_a(int arg1, int arg2, int arg3) {
	}

	@When("^Muestro el JSON del contrato$")
	public void muestro_el_JSON_del_contrato() throws Throwable {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		log.debug(mapper.writeValueAsString(contract));
	}

	@Then("^Invoco al servicio de contratacion$")
	public void invoco_al_servicio_de_contratacion() {
		contract = contractCreationGateway.process(contractCreateInfo);
		Assert.assertNotNull(contract.getId());
	}

	@Then("^Recupero el numero del contrato$")
	public void recupero_el_numero_del_contrato() {
		contractNumber = contract.getNumber();
		Assert.assertNotNull(contractNumber);
		log.debug("Contract number: {}", contract.getNumber());
	}

	@Then("^Recupero el contrato de base de datos a partir del numero$")
	public void recupero_el_contrato_de_base_de_datos_a_partir_del_numero() {
		contract = contractRepository.findByNumber(contractNumber);
		Assert.assertNotNull(contract);
	}

	@Then("^Verifico que el suscriptor es (\\w+)$")
	public void verifico_que_el_suscriptor_es_Z(String idCardNumber) {
		List<ContractPersonRelation> relations = contract.getRelations();
		Assert.assertNotNull(relations);

		List<ContractPersonRelation> filtered = relations.stream()
				.filter(x -> RelationType.SUSCRIPTOR.equals(x.getType())).collect(Collectors.toList());

		Assert.assertTrue(filtered.size() == 1);
		Assert.assertTrue(filtered.iterator().next().getPerson().getIdCard().getNumber().equals(idCardNumber));
	}

}
