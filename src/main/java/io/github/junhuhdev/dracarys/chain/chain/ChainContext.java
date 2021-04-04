package io.github.junhuhdev.dracarys.chain.chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ChainContext {

	private final List<Command> commands;

	public ChainContext(Command command) {
		this.commands = new ArrayList<Command>(Arrays.asList(command));
	}
	public ChainContext(List<Command> commands) {
		this.commands = commands;
	}

	@SuppressWarnings("unchecked")
	public <CMD extends Command> CMD getFirst(Class<CMD> type) {
		for (Command cmd : commands) {
			if (type.isInstance(cmd)) {
				return (CMD) cmd;
			}
		}
		throw new IllegalArgumentException("Command " + type.getSimpleName() + " not found");
	}


	public void store(Command command) {
		requireNonNull(command, "Command cannot be null");
		this.commands.add(command);
	}
}
