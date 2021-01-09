package huh.enterprises.dracarys.chain.cmd;

import huh.enterprises.dracarys.chain.chain.Chain;
import huh.enterprises.dracarys.chain.chain.ChainContext;
import huh.enterprises.dracarys.chain.common.Conditional;

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
