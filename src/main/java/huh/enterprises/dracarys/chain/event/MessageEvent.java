package huh.enterprises.dracarys.chain.event;

/**
 * Generic event to wrap any kind of model
 */
public class MessageEvent<T> implements XEvent {

	private final T event;

	public MessageEvent(T event) {
		this.event = event;
	}

	public T getEvent() {
		return event;
	}

}
