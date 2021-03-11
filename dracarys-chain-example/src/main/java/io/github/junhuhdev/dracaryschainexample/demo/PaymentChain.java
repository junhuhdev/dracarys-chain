package io.github.junhuhdev.dracaryschainexample.demo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class PaymentChain implements IChain {

	public List<Class<?>> chainOfCommands() {
		return List.of(
				PaymentValidateCmd.class,
				PaymentDepositCmd.class);
	}

	@Component
	public static class PaymentValidateCmd implements ICommand {

		@Data
		public static class PaymentValidateEvent {

			private String accountId;
			private Double amount;
			private String label;

		}

		@Override
		public Optional<IContext> execute(IContext ctx) {
			return ctx.onEvent(PaymentValidateEvent.class).flatMap(event -> {
				if (isBlank(event.accountId)) {
					return exit();
				}
				return proceed();
			});
		}

	}

	@Component
	public static class PaymentDepositCmd implements ICommand {

		@Data
		public static class PaymentDepositEvent {

			private String accountId;
			private Double amount;
			private boolean fraudCheck;

		}

		@Override
		public Optional<IContext> execute(IContext ctx) {
			return ctx.onEvent(PaymentDepositEvent.class).flatMap(event -> {
				if (!event.fraudCheck) {
					return exit();
				}
				return proceed();
			});
		}

	}

}
