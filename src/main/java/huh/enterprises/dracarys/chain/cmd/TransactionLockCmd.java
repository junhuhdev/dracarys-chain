package huh.enterprises.dracarys.chain.cmd;

import huh.enterprises.dracarys.chain.chain.Chain;
import huh.enterprises.dracarys.chain.chain.ChainContext;
import huh.enterprises.dracarys.chain.event.XEvent;
import huh.enterprises.dracarys.chain.event.XEventState;
import huh.enterprises.dracarys.chain.jdbc.EventJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionLockCmd implements Command {

	private final EventJdbcRepository eventJdbcRepository;

	@Override
	public ChainContext execute(ChainContext ctx, Chain chain) throws Exception {
		try {
			ctx.store(new LockEvent());
			if (eventJdbcRepository.lock(ctx.getEventTransaction())) {
				return chain.proceed(ctx);
			}
		} catch (Exception e) {
			log.error("Could not set to processing event={}", ctx.getEventTransaction(), e);
			return ctx;
		}
		log.warn("Event is already being processed by another thread... {}", ctx.getEventTransaction());
		return ctx;
	}

	@Value
	private static class LockEvent implements XEvent {

		@Override
		public Optional<XEventState> nextState() {
			return Optional.of(XEventState.PROCESSING);
		}

	}

}
