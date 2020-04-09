package ch.ethz.systems.netbench.xpt.CalQueue;

import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.network.Packet;

public class CalQueueOutputPort extends OutputPort {
	public CalQueueOutputPort(NetworkDevice ownNetworkDevice, NetworkDevice targetNetworkDevice, Link link) {
		super(ownNetworkDevice, targetNetworkDevice, link, new CalQueue());
	}

	@Override
	public void enqueue(Packet packet) {
		// TODO Auto-generated method stub

		potentialEnqueue(packet); // Directly use
	}

}
