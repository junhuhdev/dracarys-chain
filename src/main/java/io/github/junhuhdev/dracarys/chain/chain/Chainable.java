package io.github.junhuhdev.dracarys.chain.chain;

import io.github.junhuhdev.dracarys.chain.event.EventTransaction;

public interface Chainable {

	ChainContext dispatch(Command.Request command) throws Exception;

}
