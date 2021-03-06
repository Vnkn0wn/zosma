package io.leonis.zosma.ipc.peripheral;

import io.leonis.zosma.game.Agent;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Map;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * The Class ControllerDeducer.
 *
 * @param <A> The type of {@link Agent} which is being controlled by this engine.
 * @param <C> The type of controller identifier
 * @param <O> The type of output which is generated by this engine.
 * @author Rimon Oz
 */
public interface ControllerDeducer<C extends Controller, A extends Agent, I extends Controller.MappingSupplier<C, A>, O>
    extends Deducer<I, O> {

  @Override
  default Publisher<O> apply(
      final Publisher<I> controllerPublisher
  ) {
    return Flux.from(controllerPublisher)
        .flatMapIterable(controllerSupplier -> controllerSupplier.getAgentMapping().entrySet())
        .filter(controller -> this.getControllerMapping().containsKey(controller.getKey()))
        .map(controller -> this.getControllerMapping().get(controller.getKey())
            .apply(controller.getKey()));
  }

  Map<C, ControllerHandler<C, O>> getControllerMapping();
}
