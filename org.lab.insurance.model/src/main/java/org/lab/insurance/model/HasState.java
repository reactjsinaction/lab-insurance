package org.lab.insurance.model;

import org.lab.insurance.model.jpa.engine.State;

public interface HasState<T> extends HasIdentifier<T> {

	State getCurrentState();

	void setCurrentState(State state);

}
