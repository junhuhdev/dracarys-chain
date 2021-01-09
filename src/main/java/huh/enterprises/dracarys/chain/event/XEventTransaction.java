package huh.enterprises.dracarys.chain.event;

import com.google.common.collect.Lists;
import huh.enterprises.dracarys.chain.xstream.XStreamFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class XEventTransaction implements XEvent {

	private Long id;
	private Long parentId;
	private String referenceId;
	@Builder.Default
	private XEventState state = XEventState.REGISTERED;
	private String workflow;
	private List<XEvent> events = new ArrayList<>();
	@Builder.Default
	private LocalDateTime created = LocalDateTime.now();

	public static XEventTransaction newTransaction(XEvent event, String workflow) {
		return XEventTransaction.builder()
				.workflow(workflow)
				.events(Lists.newArrayList(event))
				.build();
	}

	@SuppressWarnings("unchecked")
	public static List<XEvent> fromXml(String xml) {
		return (List<XEvent>) XStreamFactory.xstream().fromXML(xml);
	}

	public boolean isProcessing() {
		return getState().isProcessing();
	}

	public void triggerStateTransition(XEventState newState) {
		if (!state.canStateTransitionTo(newState)) {
			throw new IllegalStateException("Cannot trigger state transition from " + this.state + " to " + newState);
		}
		this.state = newState;
	}

	@SuppressWarnings("unchecked")
	public <T extends XEvent> T getFirstEvent(Class<T> event) {
		for (XEvent xEvent : events) {
			if (event.isInstance(xEvent)) {
				return (T) xEvent;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends XEvent> T getLatestEvent(Class<T> event) {
		for (int i = events.size() - 1; i >= 0; i--) {
			if (event.isInstance(events.get(i))) {
				return (T) events.get(i);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends XEvent> T getLatestEvent() {
		return (T) events.get(events.size() - 1);
	}

	public void store(XEvent event) {
		if (Objects.nonNull(event)) {
			this.events.add(event);
		}
	}

	public String toXml() {
		return XStreamFactory.xstream().toXML(getEvents());
	}

	@Override
	public String toString() {
		return "XEventTransaction{" +
				"id=" + id +
				", parentId=" + parentId +
				", state=" + state +
				", workflow='" + workflow + '\'' +
				'}';
	}

}
