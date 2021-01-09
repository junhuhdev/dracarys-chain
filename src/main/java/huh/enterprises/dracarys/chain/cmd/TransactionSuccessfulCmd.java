package huh.enterprises.dracarys.chain.cmd;

import com.tele2.orderflow.mcls.mclsparent.chain.Chain;
import com.tele2.orderflow.mcls.mclsparent.chain.ChainContext;
import com.tele2.orderflow.mcls.mclsparent.event.XEvent;
import com.tele2.orderflow.mcls.mclsparent.event.XEventState;
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
		if (ctx.getState().canStateTransitionTo(XEventState.SUCCESSFUL)) {
			ctx.store(new SuccessfulEvent());
		}
		return chain.proceed(ctx);
	}

	@Value
	private static class SuccessfulEvent implements XEvent {

		@Override
		public Optional<XEventState> nextState() {
			return Optional.of(XEventState.SUCCESSFUL);
		}

	}

}
