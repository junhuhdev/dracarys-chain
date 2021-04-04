package io.github.junhuhdev.dracarys.chain.chain;

import io.github.junhuhdev.dracarys.chain.event.EventLambda;
import io.github.junhuhdev.dracarys.chain.event.EventTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ChainRouter implements Chainable {

	private final Logger log = LoggerFactory.getLogger(ChainRouter.class);

	private final List<ChainBase> chains;

	public ChainRouter(List<ChainBase> chains) {
		this.chains = chains;
	}

	@Override
	public ChainContext dispatch(Command.Request cmd) throws Exception {
		var chainMatches = chains.stream()
				.filter(chain -> chain.matches(cmd))
				.collect(toList());
		log.info("--> Routing to chains {}", chainMatches.stream().map(r -> r.getClass().getSimpleName()).collect(toList()));
		if (chainMatches.size() > 1) {
			throw new IllegalStateException(String.format("Found more than one chain to route type=%s", cmd.getClass().getSimpleName()));
		}
		if (isEmpty(chainMatches)) {
			throw new IllegalStateException("No chain to process workflow=" + cmd.getClass().getSimpleName());
		}
		var chain = chainMatches.stream().findFirst().orElseThrow();
		return chain.dispatch(cmd);
	}

	public void enqueue(EventLambda event) {

	}

}
