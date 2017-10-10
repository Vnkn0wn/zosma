package io.leonis.zosma.math.control.anomaly;

import io.leonis.zosma.math.control.*;
import io.leonis.zosma.math.statistic.*;
import java.util.stream.Collectors;
import lombok.Value;

/**
 * The Class HBOSAnomalyDetector.
 *
 * This class describes an {@link AnomalyDetector} which uses the <a
 * href="http://www.dfki.de/KI2012/PosterDemoTrack/ki2012pd13.pdf">HBOS algorithm</a>.
 *
 * @author Rimon Oz
 */
@Value
public class HBOSAnomalyDetector implements ScanningAnomalyDetector<Histogram, Number> {
  private final double weight;
  private final double maximumScore;

  @Override
  public boolean test(final Histogram inputSpaceRepresentation, final Number input) {
    return this.computeHBOS(inputSpaceRepresentation, input).doubleValue() > this.getMaximumScore();
  }

  /**
   * @param inputSpaceRepresentation The measurement space representation as a {@link Histogram}.
   * @param value                    The value to compute the HBOS of.
   * @return The Histogram-Based Outlier Score (HBOS) according to the supplied measurement space.
   */
  protected Number computeHBOS(final Histogram inputSpaceRepresentation, final Number value) {
    final Histogram normalizedRepresentation = this.normalizeVertically(inputSpaceRepresentation);
    final Double binHeight = normalizedRepresentation.getBins()
        .get((int) Math.floor((value.doubleValue() - normalizedRepresentation.getLowerBound())
            / normalizedRepresentation.getBinWidth()));
    return Math.log(1 / binHeight);
  }

  /**
   * @param toNormalize The histogram to normalize.
   * @return The (vertically) normalized representation of this histogram, such that the largest
   * bin(s) receive(s) a normalized weight of 1.
   */
  protected Histogram normalizeVertically(final Histogram toNormalize) {
    return toNormalize.getBins().stream()
        .max(Double::compare)
        .<Histogram>map(maximum -> new SimpleHistogram(
            toNormalize.getLowerBound(),
            toNormalize.getBinWidth(),
            toNormalize.getBins().stream()
                .map(value -> value / maximum)
                .collect(Collectors.toList())))
        .orElse(toNormalize);
  }
}