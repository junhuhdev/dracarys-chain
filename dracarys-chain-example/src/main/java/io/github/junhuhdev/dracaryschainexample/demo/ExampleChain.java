package io.github.junhuhdev.dracaryschainexample.demo;

import io.github.junhuhdev.dracarys.chain.chain.Chain;
import io.github.junhuhdev.dracarys.chain.chain.ChainBase;
import io.github.junhuhdev.dracarys.chain.chain.ChainContext;
import io.github.junhuhdev.dracarys.chain.cmd.Command;

public class ExampleChain extends ChainBase {

	@Override
	protected Class<?>[] getCommands() {
		return new Class[]{
				Command1.class
		};
	}

	public static class Command1 implements Command {

		@Override
		public ChainContext execute(ChainContext ctx, Chain chain) throws Exception {
			return null;
		}

	}

}
