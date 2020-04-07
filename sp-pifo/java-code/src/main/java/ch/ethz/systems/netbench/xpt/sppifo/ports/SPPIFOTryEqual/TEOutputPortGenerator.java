package ch.ethz.systems.netbench.xpt.sppifo.ports.SPPIFOTryEqual;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.run.infrastructure.OutputPortGenerator;

public class TEOutputPortGenerator extends OutputPortGenerator {

	private final long numberQueues;
	private final long sizePerQueuePackets;
	private final String stepSize;

	public TEOutputPortGenerator(long numberQueues, long sizePerQueuePackets, String stepSize) {
		this.numberQueues = numberQueues;
		this.sizePerQueuePackets = sizePerQueuePackets;
		this.stepSize = stepSize;
		SimulationLogger.logInfo("Port", "TEPIFO(numberQueues=" + numberQueues + ", sizePerQueuePackets="
				+ sizePerQueuePackets + ", stepSize=" + stepSize + ")");
	}

	@Override
	public OutputPort generate(NetworkDevice ownNetworkDevice, NetworkDevice towardsNetworkDevice, Link link) {
		return new TEOutputPort(ownNetworkDevice, towardsNetworkDevice, link, numberQueues, sizePerQueuePackets,
				stepSize);
	}

}
