package org.ssh.benchmarks.report;

import java.util.List;
import org.ssh.benchmarks.Profiler;

/**
 * The Interface Report.
 * <p>
 * This interface represents a report and is generally generated by a {@link Profiler}.
 *
 * @author Rimon Oz
 */
public interface Report {

  /**
   * Returns the title of the report.
   *
   * @return The title of the report.
   */
  String getTitle();

  /**
   * Returns the ordered list of chapters in this report.
   *
   * @return The ordered list of chapters in this report.
   */
  List<Chapter> getChapters();
}
