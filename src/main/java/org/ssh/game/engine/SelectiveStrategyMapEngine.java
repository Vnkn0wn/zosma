package org.ssh.game.engine;

import org.ssh.game.*;

import java.util.stream.Collectors;

/**
 * The Interface SelectiveStrategyMapEngine.
 *
 * @param <M> The type of object used to identify an engine part.
 * @param <S> The type of {@link Strategy} which this {@link Engine} produces.
 * @param <G> The type of {@link Game} for which {@link Strategy} is being generated.
 * @author Rimon Oz
 */
public interface SelectiveStrategyMapEngine<M, S extends Strategy<? extends Agent, ? extends Command, G>, G extends Game>
        extends MapEngine<M, S, SelectiveStrategizer<M, S, G>>,
        SelectiveStrategyEngine<M, S, G> {
    /**
     * Computes a strategy based on the previous {@link Strategy} and the most recent {@link Game game state}. The
     * default implementation ignores the previous strategy and computes a strategy by arbitrating strategies generated
     * by each individual strategizer in the Engine.
     *
     * @param previousStrategy The previously emitted {@link Strategy}.
     * @param game             The most recent {@link Game game state}.
     * @return The newest {@link Strategy}.
     */
    @Override
    default S scan(S previousStrategy, G game, M subjects) {
        return this.arbitrate(this.getParts().stream()
                .collect(Collectors.toMap(
                        strategizer -> strategizer,
                        strategizer -> strategizer.scan(previousStrategy, game, subjects))));
    }
}
