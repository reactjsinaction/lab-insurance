package org.lab.insurance.engine;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.lab.insurance.core.serialization.Serializer;
import org.lab.insurance.model.common.Message;
import org.lab.insurance.model.engine.ActionDefinition;
import org.lab.insurance.model.engine.ActionEntity;
import org.lab.insurance.model.exceptions.CancelAndRexecuteException;
import org.lab.insurance.model.jpa.engine.ActionExecution;
import org.lab.insurance.services.common.TimestampProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class ActionExecutionService {

	private static final Logger LOG = LoggerFactory.getLogger(ActionExecutionService.class);

	@Inject
	private CamelContext camelContext;
	@Inject
	private Provider<EntityManager> entityManagerProvider;
	@Inject
	private Serializer serializer;
	@Inject
	private TimestampProvider timestampProvider;
	@Inject
	private ActionPriorityMapper actionPriorityMapper;

	@Transactional
	@SuppressWarnings("unchecked")
	public <T> Message<T> execute(ActionEntity<T> actionEntity) {
		LOG.debug(StringUtils.rightPad(String.format("-- Processing %s ", actionEntity.getClass().getSimpleName()), 80, '-'));
		ActionDefinition definition = actionEntity.getClass().getAnnotation(ActionDefinition.class);
		Validate.notNull(definition, String.format("Entity class %s has not ActionDefinition anotation", actionEntity.getClass().getName()));
		String endpoint = definition.endpoint();
		ProducerTemplate producer = camelContext.createProducerTemplate();
		ActionExecution actionExecution = buildActionExecutionEntity(actionEntity);
		EntityManager entityManager = entityManagerProvider.get();
		try {
			Message<T> result = producer.requestBody(endpoint, actionEntity, Message.class);
			producer.stop();
			actionExecution.setExecuted(timestampProvider.getCurrentDateTime());
			actionExecution.setResultJson(serializer.toJson(result));
			entityManager.persist(actionExecution);
			return result;
		} catch (CancelAndRexecuteException ex) {
			LOG.error("Cancel and re-execution error", ex);
			entityManager.getTransaction().rollback();
			entityManager.getTransaction().begin();
			actionExecution.setFailure(timestampProvider.getCurrentDateTime());
			actionExecution.setCancelled(timestampProvider.getCurrentDateTime());
			entityManager.persist(actionExecution);
			entityManager.getTransaction().commit();
			entityManager.getTransaction().begin();
			schedule(ex.getActionEntity(), ex.getScheduled());
			Message<T> result = new Message<T>();
			result.setCode(Message.REEXECUTION_ERROR);
			result.setMessage(ex.getMessage());
			return result;
		} catch (Exception ex) {
			LOG.error("Action execution error", ex);
			entityManager.getTransaction().rollback();
			entityManager.getTransaction().begin();
			actionExecution.setFailure(timestampProvider.getCurrentDateTime());
			// TODO add exception info
			actionExecution.setResultJson(serializer.toJson(ex.getMessage()));
			entityManager.persist(actionExecution);
			entityManager.getTransaction().commit();
			entityManager.getTransaction().begin();
			throw new RuntimeException(ex);
		}
	}

	@Transactional
	public void schedule(ActionEntity<?> actionEntity, Date when) {
		Integer priority = actionPriorityMapper.getPriority(actionEntity);
		ActionExecution actionExecution = buildActionExecutionEntity(actionEntity);
		actionExecution.setScheduled(when);
		actionExecution.setPriority(priority);
		EntityManager entityManager = entityManagerProvider.get();
		entityManager.persist(actionExecution);
	}

	private <T> ActionExecution buildActionExecutionEntity(ActionEntity<T> actionEntity) {
		ActionExecution actionExecution = new ActionExecution();
		actionExecution.setActionEntityClass(actionEntity.getClass());
		actionExecution.setActionEntityJson(serializer.toJson(actionEntity));
		return actionExecution;
	}
}
