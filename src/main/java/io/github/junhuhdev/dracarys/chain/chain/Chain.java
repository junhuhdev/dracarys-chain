package io.github.junhuhdev.dracarys.chain.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ListIterator;

public class Chain {

	private final Logger log = LoggerFactory.getLogger(Chain.class);
	private final ListIterator<Command.Handler> commands;

	public Chain(ListIterator<Command.Handler> commands) {
		this.commands = commands;
	}

	public ChainContext proceed(ChainContext ctx) throws Exception {
		if (!commands.hasNext()) {
			throw new IllegalStateException("No command configured to handle end of chain gracefully.");
		}
		Command.Handler command = commands.next();
		var cmdHandlerName = command.getClass().getName().substring(command.getClass().getName().lastIndexOf('.') + 1);
		var cmdName = cmdHandlerName.split("\\$")[0];
		try {
			log.info("--> Started cmd={}", cmdName);
			return command.execute(ctx, this);
		} catch (Exception e) {
			log.error("<-- Failed cmd={}", cmdName, e);
			throw e;
		} finally {
			log.info("<-- Completed cmd={}", cmdName);
			commands.previous();
		}
	}

}
