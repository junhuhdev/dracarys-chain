package io.github.junhuhdev.dracarys.chain.cmd;

import io.github.junhuhdev.dracarys.chain.chain.Chain;
import io.github.junhuhdev.dracarys.chain.chain.ChainContext;
import io.github.junhuhdev.dracarys.chain.event.Event;
import io.github.junhuhdev.dracarys.chain.event.EventState;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionSuccessfulCmd implements Command {

	@Override
	public ChainContext execute(ChainContext ctx, Chain chain) throws Exception {
		if (ctx.getState().canStateTransitionTo(EventState.SUCCESSFUL)) {
			ctx.store(new SuccessfulEvent());
		}
		return chain.proceed(ctx);
	}

	@Value
	private static class SuccessfulEvent implements Event {

		@Override
		public Optional<EventState> nextState() {
			return Optional.of(EventState.SUCCESSFUL);
		}

	}

}