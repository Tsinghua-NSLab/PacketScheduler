package ch.ethz.systems.netbench.xpt.CalQueue;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.run.infrastructure.OutputPortGenerator;

public class CalQueueOutputPortGenerator extends OutputPortGenerator {

	public CalQueueOutputPortGenerator() {
		SimulationLogger.logInfo("Port", "Create CalQueue OutputPort");
	}

	@Override
	public OutputPort generate(NetworkDevice ownNetworkDevice, NetworkDevice towardsNetworkDevice, Link link) {
		return new CalQueueOutputPort(ownNetworkDevice, towardsNetworkDevice, link);
	}

}
