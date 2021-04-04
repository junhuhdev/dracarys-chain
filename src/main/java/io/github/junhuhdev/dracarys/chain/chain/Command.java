package io.github.junhuhdev.dracarys.chain.chain;

/**
 * Default interface for all commands to implement.
 */
public interface Command {

	interface Request extends Command {

	}

	interface Handler extends Command {

		ChainContext execute(ChainContext ctx, Chain chain) throws Exception;

	}

}
