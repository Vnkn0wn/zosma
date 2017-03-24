package org.ssh.ipc.system;

import org.reactivestreams.Publisher;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * The Class RamInfoPublisher.
 *
 * This class describes a {@link org.reactivestreams.Publisher} of system information.
 *
 * @author Jeroen de Jong
 */
public abstract class SystemInfoPublisher<T> implements Publisher<T> {
    /** The {@link SystemInfo source} of the state information. **/
    protected final SystemInfo systemInfo = new SystemInfo();
    /** The {@link HardwareAbstractionLayer source} of the hardware state information. **/
    protected final HardwareAbstractionLayer hal = this.systemInfo.getHardware();
    /** The INTERVAL between new data in ms */
    protected static final Integer INTERVAL = 500;
}
