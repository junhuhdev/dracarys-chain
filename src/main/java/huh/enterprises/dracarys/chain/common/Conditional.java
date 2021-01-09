package huh.enterprises.dracarys.chain.common;

import huh.enterprises.dracarys.chain.event.XEventTransaction;

import java.util.List;

import static java.util.Collections.emptyList;

public interface Conditional {

	/** Returns boolean flag if chain can process event.*/
	default boolean canProcess(XEventTransaction event) {
		return true;
	}

	default List<String> canProcessWorkflows() {
		return emptyList();
	}

}
