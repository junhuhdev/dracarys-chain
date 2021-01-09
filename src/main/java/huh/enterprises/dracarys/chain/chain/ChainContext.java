package huh.enterprises.dracarys.chain.chain;

import com.google.common.collect.Lists;
import huh.enterprises.dracarys.chain.event.XEvent;
import huh.enterprises.dracarys.chain.event.XEventState;
import huh.enterprises.dracarys.chain.event.XEventTransaction;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class ChainContext {

	private final XEventTransaction xEventTransaction;

	public ChainContext(XEventTransaction xEventTransaction) {
		this.xEventTransaction = xEventTransaction;
	}

	public XEventTransaction getEventTransaction() {
		return xEventTransaction;
	}

	public void setNextWorkflow(String workflow) {
		xEventTransaction.setWorkflow(workflow);
	}

	public String getWorkflow() {
		return xEventTransaction.getWorkflow();
	}

	public Long getId() {
		return xEventTransaction.getId();
	}

	public Long getParentId() {
		return xEventTransaction.getParentId();
	}

	public Long getOriginId() {
		return isNull(xEventTransaction.getParentId()) ? xEventTransaction.getId() : xEventTransaction.getParentId();
	}

	public List<XEvent> getEvents() {
		return xEventTransaction.getEvents();
	}

	public XEventState getState() {
		return xEventTransaction.getState();
	}

	public <T extends XEvent> T getFirstEvent(Class<T> event) {
		return this.xEventTransaction.getFirstEvent(event);
	}

	public <T extends XEvent> T getLatestEvent(Class<T> event) {
		return this.xEventTransaction.getLatestEvent(event);
	}

	public <T extends XEvent> T getLatestEvent() {
		return this.xEventTransaction.getLatestEvent();
	}

	public XEventTransaction newChild(XEvent nextEvent, String nextWorkflow) {
		return XEventTransaction.builder()
				.parentId(getOriginId())
				.workflow(nextWorkflow)
				.events(Lists.newArrayList(nextEvent))
				.build();
	}

	public void store(XEvent event) {
		requireNonNull(event, "Event cannot be null");
		this.xEventTransaction.store(event);
		event.nextUpdate(this.xEventTransaction);
		var onNextState = event.nextState();
		onNextState.ifPresent(this.xEventTransaction::triggerStateTransition);
	}

}
