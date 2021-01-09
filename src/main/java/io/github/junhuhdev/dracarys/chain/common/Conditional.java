package io.github.junhuhdev.dracarys.chain.common;

import io.github.junhuhdev.dracarys.chain.event.EventTransaction;

import java.util.List;

import static java.util.Collections.emptyList;

public interface Conditional {

	/** Returns boolean flag if chain can process event.*/
	default boolean canProcess(EventTransaction event) {
		return true;
	}

	default List<String> canProcessWorkflows() {
		return emptyList();
	}

}
