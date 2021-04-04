package io.github.junhuhdev.dracarys.chain.chain;

import jakarta.enterprise.inject.spi.CDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * One chain per event to process
 */
public abstract class ChainBase<REQUEST extends Command.Request> implements Chainable {

	private final Logger log = LoggerFactory.getLogger(ChainBase.class);

	@Resource
	private ListableBeanFactory beanFactory;

	protected abstract List<Class<? extends Command>> listOfCommands();

	@Override
	public ChainContext dispatch(Command.Request command) throws Exception {
		ListIterator<Command.Handler> commands = injectCommands();
		Chain chain = new Chain(commands);
		return chain.proceed(new ChainContext(command));
	}

	protected boolean matches(REQUEST request) {
		Class handlerType = getClass();
		Class commandType = request.getClass();
		return new FirstGenericArgOf(handlerType).isAssignableFrom(commandType);
	}

	private ListIterator<Command.Handler> injectCommands() {
		List<Command.Handler> commands = new ArrayList<>();
		for (var cmd : listOfCommands()) {
			var clazz = Arrays.stream(cmd.getDeclaredClasses())
					.filter(r -> r.getSimpleName().equalsIgnoreCase("Handler"))
					.findFirst();
			clazz.ifPresentOrElse(handlerClazz -> {
				var bean = (Command.Handler) CDI.current().select(handlerClazz).get();
				// log.info("--> Registered cmd={}", cmd);
				commands.add(bean);
			}, () -> log.error("<-- Failed to register cmd={}", cmd));
		}
		return commands.listIterator();
	}

}
