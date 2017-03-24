package org.ssh.game.engine;

import org.ssh.game.*;

import java.util.stream.Collectors;

/**
 * The Interface StrategyEngine.
 * <p>
 * This interface describes the functionality of an {@link Engine} which produces
 * {@link Strategy} for a specific semantic triple ({@link Agent}, {@link Command}, {@link Game}).
 *
 * @param <S> The type of {@link Strategy} which this {@link Engine} produces.
 * @param <G> The type of {@link Game} for which {@link Strategy} is being generated.
 * @author Rimon Oz
 */
public interface StrategyEngine<S extends Strategy<? extends Agent, ? extends Command, G>, G extends Game>
        extends ProcessorEngine<G, S, Strategizer<S, G>> {
    /**
     * Computes a strategy based on the previous {@link Strategy} and the most recent {@link Game game state}. The
     * default implementation ignores the previous strategy and computes a strategy by arbitrating strategies generated
     * by each individual strategizer in the Engine.
     *
     * @param previousStrategy The previously emitted {@link Strategy}.
     * @param game             The most recent {@link Game game state}.
     * @return The newest {@link Strategy}.
     */
    default S scan(S previousStrategy, G game) {
        return this.arbitrate(this.getParts().stream()
                .collect(Collectors.toMap(
                        strategizer -> strategizer,
                        strategizer -> strategizer.scan(previousStrategy, game))));
    }
}
