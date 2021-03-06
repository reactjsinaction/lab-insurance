package org.lab.insurance.engine;

import java.util.Date;

import org.lab.insurance.core.serialization.Serializer;
import org.lab.insurance.model.engine.ActionExecution;
import org.lab.insurance.services.common.TimestampProvider;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionExecutionRunner {

	@Autowired
	private ActionExecutionService actionExecutionService;
	@Autowired
	private Serializer serializer;
	@Autowired
	private TimestampProvider timeStampProvider;

	public void run(Date from, Date to) {
		throw new RuntimeException("Not implemented jpa -> mongo");
		// EntityManager entityManager = entityManagerProvider.get();
		// CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		// CriteriaQuery<ActionExecution> criteriaQuery = builder.createQuery(ActionExecution.class);
		// Root<ActionExecution> root = criteriaQuery.from(ActionExecution.class);
		// Predicate predicate = builder.isNull(root.<Date>get("cancelled"));
		// predicate = builder.and(predicate, builder.isNull(root.<Date>get("executed")));
		// predicate = builder.and(predicate, builder.isNull(root.<Date>get("failure")));
		// predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.<Date>get("scheduled"), from));
		// predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.<Date>get("scheduled"), to));
		// criteriaQuery.where(predicate);
		// criteriaQuery.orderBy(builder.asc(root.<Date>get("scheduled")), builder.asc(root.<Integer>get("priority")));
		// TypedQuery<ActionExecution> query = entityManager.createQuery(criteriaQuery);
		// query.setMaxResults(1);
		// query.setParameter("from", from);
		// query.setParameter("to", to);
		// while (true) {
		// List<ActionExecution> list = query.getResultList();
		// if (list.isEmpty()) {
		// break;
		// }
		// else {
		// ActionExecution actionExecution = list.iterator().next();
		// executeActionExecution(actionExecution, entityManager);
		// entityManager.detach(actionExecution);
		// entityManager.clear();
		// }
		// }
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void executeActionExecution(ActionExecution actionExecution) {
		log.debug("Executing scheduled task {}", actionExecution.getActionEntityClass().getSimpleName());
		throw new RuntimeException("Not implemented jpa -> mongo");
		// try {
		// Class<?> actionEntityClass = actionExecution.getActionEntityClass();
		// ActionEntity actionEntity = (ActionEntity) serializer.fromJson(actionExecution.getActionEntityJson(),
		// actionEntityClass);
		// actionExecutionService.execute(actionEntity);
		// actionExecution.setExecuted(timeStampProvider.getCurrentDateTime());
		// entityManager.merge(actionExecution);
		// }
		// catch (Exception ex) {
		// LOG.error("Execution error", ex);
		// entityManager.getTransaction().rollback();
		// entityManager.getTransaction().begin();
		// actionExecution.setFailure(timeStampProvider.getCurrentDateTime());
		// entityManager.merge(actionExecution);
		// entityManager.getTransaction().commit();
		// entityManager.getTransaction().begin();
		// }
		// finally {
		// entityManager.flush();
		// }
	}
}
