package io.github.junhuhdev.dracarys.chain.chain;

public interface Chainable {

	ChainContext dispatch(Command.Request command) throws Exception;

}
