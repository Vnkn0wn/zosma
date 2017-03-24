package org.ssh.ipc.peripheral;

import com.google.common.collect.Sets;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.ssh.game.Agent;
import org.ssh.game.Command;
import org.ssh.game.Game;
import org.ssh.game.Strategy;
import org.ssh.game.engine.MapEngine;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * The Class ControllerEngine.
 *
 * @param <A> the type parameter
 * @param <S> the type parameter
 * @param <G> the type parameter
 * @author Rimon Oz
 */
@Slf4j
public abstract class ControllerEngine<
    A extends Agent,
    S extends Strategy,
    G extends Game> implements MapEngine<Set<A>, S, Integer> {

  /**
   * The Controllers.
   */
  protected final ControllerManager controllers;

  /**
   * The publisher on which controller states are being published.
   */
  private final Publisher<Map<Integer, ControllerState>> controllerStatePublisher;

  /**
   * Instantiates a new ControllerEngine.
   *
   * @param controllerCount The number of controllers.
   */
  public ControllerEngine(int controllerCount) {
    this.controllers = new ControllerManager(controllerCount);
    this.controllers.initSDLGamepad();
    this.controllerStatePublisher = Flux.create(fluxSink -> {
      while (true) {
        fluxSink.next(IntStream.range(0, controllerCount)
            .boxed()
            .filter(controllerIndex -> this.controllers.getControllerIndex(controllerIndex)
                .isConnected())
            .collect(Collectors.toMap(
                Function.identity(),
                controllers::getState)));
      }
    });
  }

  /**
   * Returns a set of players which are controlled by this engine.
   *
   * @return the controlled players
   */
  public abstract Set<A> getControllableAgents();

  /**
   * Assigns the supplied handler to the supplied {@link Agent}.
   *
   * @param handler The handler to link to a {@link Agent}.
   * @param agent   The {@link Agent} to link the handler to.
   * @return set set
   */
  public Set<A> assignHandler(Integer handler, A agent) {
    log.info("Assigning handler '" + handler + "' to "
        + agent.getClass().getTypeName() + " " + agent.getId());
    return this.getPartsMap().put(handler, Sets.union(Collections.singleton(agent),
        this.getPartsMap().getOrDefault(handler, Collections.emptySet())));
  }

  /**
   * Unassigns a handler from the specified {@link Agent}.
   *
   * @param handler The handler to unassign from the {@link Agent}.
   * @param agent   The {@link Agent} to unassign the handler from.
   * @return set set
   */
  public Set<A> unassignHandler(Integer handler, A agent) {
    log.info("Unassigning handler '" + handler + "' from "
        + agent.getClass().getTypeName() + " " + agent.getId());
    if (this.getPartsMap().containsKey(handler)) {
      if (this.getPartsMap().get(handler).contains(agent)) {
        return this.getPartsMap().put(
            handler,
            Sets.difference(this.getPartsMap().get(handler), Collections.singleton(agent)));
      }
    }
    return Collections.emptySet();
  }

  @Override
  public void subscribe(Subscriber<? super S> subscriber) {
    Flux.from(this.controllerStatePublisher)
        .subscribeOn(Schedulers.single())
        .map(mapping -> mapping.entrySet().stream()
            .filter(entry -> this.getPartsMap().containsKey(entry.getKey()))
            .flatMap(entry -> this.getPartsMap().get(entry.getKey()).stream()
                .collect(Collectors.toMap(Function.identity(), a -> entry))
                .entrySet().stream())
            .collect(Collectors.toMap(
                entry -> entry.getValue().getKey(),
                entry -> this.createStrategy(entry.getKey(), entry.getValue().getValue()))))
        .map(this::arbitrate)
        // merge the emissions of multiple controllers to the same controllable
        .subscribe(subscriber);
  }

  /**
   * Creates a {@link Strategy} from the specified command for the specified agent.
   *
   * @param agent   The {@link Agent} to create a strategy for.
   * @param command The {@link Command} to create the strategy from.
   * @return A strategy for the supplied {@link Agent} created from the supplied {@link Command}
   */
  public abstract S createStrategy(A agent, ControllerState command);
}
