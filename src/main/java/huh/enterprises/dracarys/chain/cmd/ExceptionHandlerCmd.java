package huh.enterprises.dracarys.chain.cmd;

import com.tele2.orderflow.mcls.mclsparent.chain.Chain;
import com.tele2.orderflow.mcls.mclsparent.chain.ChainContext;
import com.tele2.orderflow.mcls.mclsparent.event.FaultEvent;
import com.tele2.orderflow.mcls.mclsparent.jdbc.EventJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ExceptionHandlerCmd implements com.tele2.orderflow.mcls.mclsparent.command.Command {

	private final EventJdbcRepository eventJdbcRepository;

	@Override
	public ChainContext execute(ChainContext ctx, Chain chain) {
		try {
			return chain.proceed(ctx);
		} catch (Exception e) {
			log.error("Exception was thrown while processing event={}", ctx.getEventTransaction(), e);
			ctx.store(new FaultEvent(e));
			eventJdbcRepository.update(ctx.getEventTransaction());
		}
		return ctx;
	}

}
