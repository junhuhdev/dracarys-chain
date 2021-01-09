package io.github.junhuhdev.dracarys.chain.cmd;

import io.github.junhuhdev.dracarys.chain.chain.Chain;
import io.github.junhuhdev.dracarys.chain.chain.ChainContext;
import org.springframework.stereotype.Component;

/**
 * Command that indicates end of chain process
 */
@Component
public class FinalizeCmd implements Command {

    @Override
    public ChainContext execute(ChainContext ctx, Chain chain) {
        return ctx;
    }
}
