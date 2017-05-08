package org.ssh.benchmarks;

import lombok.Value;
import org.ssh.math.statistic.DescriptiveMeasure;
import org.ssh.math.statistic.DescriptiveMeasure.Count;
import org.ssh.math.unit.Unit;
import org.ssh.math.unit.Units;

/**
 * The Class SimpleMeasurement
 *
 * A simple measurement consisting of a single value without context.
 *
 * @param <V> The type of value.
 * @author Jeroen de Jong
 */
@Value
public class SimpleMeasurement<V> implements DescriptiveMeasurement<V> {
  private final V value;
  private final DescriptiveMeasure type = Count.TOTAL;
  private final String label;
  private final Unit unit = Units.UNIT;
}