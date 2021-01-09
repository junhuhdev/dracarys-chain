package io.github.junhuhdev.dracarys.chain.cmd;

import io.github.junhuhdev.dracarys.chain.chain.Chain;
import io.github.junhuhdev.dracarys.chain.chain.ChainContext;
import io.github.junhuhdev.dracarys.chain.common.Conditional;

/**
 * Default interface for all commands to implement.
 */
public interface Command extends Conditional {

	/**
	 * (1) Proceed to next command "chain.proceed(ctx);"
	 * (2) Do not proceed to next command "return ctx"
	 */
	ChainContext execute(ChainContext ctx, Chain chain) throws Exception;

}
