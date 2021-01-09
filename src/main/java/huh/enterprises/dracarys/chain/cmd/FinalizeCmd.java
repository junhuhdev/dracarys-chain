package huh.enterprises.dracarys.chain.cmd;

import com.tele2.orderflow.mcls.mclsparent.chain.Chain;
import com.tele2.orderflow.mcls.mclsparent.chain.ChainContext;
import org.springframework.stereotype.Component;

/**
 * Command that indicates end of chain process
 */
@Component
public class FinalizeCmd implements com.tele2.orderflow.mcls.mclsparent.command.Command {

	@Override
	public ChainContext execute(ChainContext ctx, Chain chain) {
		return ctx;
	}

}
