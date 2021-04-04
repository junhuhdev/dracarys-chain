package io.github.junhuhdev.dracarys.chain.cmd;

import io.github.junhuhdev.dracarys.chain.chain.Chain;
import io.github.junhuhdev.dracarys.chain.chain.ChainContext;
import io.github.junhuhdev.dracarys.chain.chain.Command;
import io.github.junhuhdev.dracarys.chain.jdbc.EventJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionSaveAsLastCmd implements Command {

	private final EventJdbcRepository eventJdbcRepository;

	@Override
	public ChainContext execute(ChainContext ctx, Chain chain) throws Exception {
		ctx = chain.proceed(ctx);
		eventJdbcRepository.update(ctx.getEventTransaction());
		return ctx;
	}

}
