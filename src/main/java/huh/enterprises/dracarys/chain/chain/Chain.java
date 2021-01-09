package huh.enterprises.dracarys.chain.chain;

import huh.enterprises.dracarys.chain.cmd.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ListIterator;

public class Chain {

	private static final Logger log = LoggerFactory.getLogger(Chain.class);
	private final ListIterator<Command> commands;

	public Chain(ListIterator<Command> commands) {
		this.commands = commands;
	}

	/** Recusively calls itself until all cmds of chain have executed */
	public ChainContext proceed(ChainContext ctx) throws Exception {
		if (!commands.hasNext()) {
			return completeChain();
		}
		Command command = commands.next();
		String cmdClazz = command.getClass().getSimpleName();
		try {
			log.info("Processing command={} id={} state={} workflow={}", cmdClazz, ctx.getId(), ctx.getState(), ctx.getWorkflow());
			return command.execute(ctx, this);
		} catch (Exception e) {
			log.error("Failed to process command={}, event={}", cmdClazz, ctx.getEventTransaction(), e);
			throw e;
		} finally {
			commands.previous();
		}
	}

	private ChainContext completeChain() {
		throw new IllegalStateException("No command configured to handle end of chain gracefully.");
	}

}