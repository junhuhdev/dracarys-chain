package huh.enterprises.dracarys.chain.chain;

import huh.enterprises.dracarys.chain.cmd.Command;
import huh.enterprises.dracarys.chain.cmd.ExceptionHandlerCmd;
import huh.enterprises.dracarys.chain.cmd.FinalizeCmd;
import huh.enterprises.dracarys.chain.cmd.TransactionLockCmd;
import huh.enterprises.dracarys.chain.cmd.TransactionSaveAsLastCmd;
import huh.enterprises.dracarys.chain.cmd.TransactionSuccessfulCmd;
import huh.enterprises.dracarys.chain.common.Conditional;
import huh.enterprises.dracarys.chain.event.XEventTransaction;
import huh.enterprises.dracarys.chain.jdbc.EventJdbcRepository;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * One chain per event to process
 */
public abstract class ChainBase implements Chainable, Conditional {

	@Resource
	private ListableBeanFactory beanFactory;
	@Autowired
	private PreChain preChain;
	@Autowired
	private PostChain postChain;
	@Autowired
	private EventJdbcRepository eventJdbcRepository;

	protected abstract Class<?>[] getCommands();

	private ListIterator<Command> createCommands() {
		List<Command> commands = new ArrayList<>();
		addCommands(commands, preChain.getCommands());
		addCommands(commands, this.getCommands());
		addCommands(commands, postChain.getCommands());
		return commands.listIterator();
	}

	private void addCommands(List<Command> commands, Class<?>[] listOfCmds) {
		for (Class<?> clazz : listOfCmds) {
			Command bean = (Command) beanFactory.getBean(clazz);
			commands.add(bean);
		}
	}

	@Override
	public ChainContext resume(String referenceId) throws Exception {
		var event = eventJdbcRepository.findByReferenceId(referenceId);
		return dispatch(event);
	}

	@Override
	public ChainContext dispatch(XEventTransaction event) throws Exception {
		ListIterator<Command> commands = this.createCommands();
		Chain chain = new Chain(commands);
		return chain.proceed(new ChainContext(event));
	}

	@Component
	static class PreChain extends ChainBase {

		@Override
		protected Class<?>[] getCommands() {
			return new Class[]{
					TransactionLockCmd.class,
					ExceptionHandlerCmd.class,
					TransactionSaveAsLastCmd.class,
			};
		}

		@Override
		public boolean isMixable() {
			return false;
		}

	}

	@Component
	static class PostChain extends ChainBase {

		@Override
		protected Class<?>[] getCommands() {
			return new Class[]{
					TransactionSuccessfulCmd.class,
					FinalizeCmd.class
			};
		}

		@Override
		public boolean isMixable() {
			return false;
		}

	}

}
