package io.github.junhuhdev.dracaryschainexample.demo;

import java.util.Optional;

public interface ICommand {

	Optional<IContext> execute(IContext ctx);

	default Optional<? extends IContext> proceed() {
		return Optional.empty();
	}

	default Optional<? extends IContext> exit() {
		return Optional.empty();
	}

	default String toXml() {
		return "";
	}
}
