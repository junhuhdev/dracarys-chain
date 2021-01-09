package huh.enterprises.dracarys.chain.event;

import java.util.Optional;

public interface XEvent {

	/** Optionally override this method when nextState transition is required */
	default Optional<XEventState> nextState() {
		return Optional.empty();
	}

	default void nextUpdate(XEventTransaction xEventTransaction) {
	}

}
